/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelAccess;
import org.epics.pvaccess.client.ChannelAccessFactory;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelPutGet;
import org.epics.pvaccess.client.ChannelPutGetRequester;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvdata.copy.CreateRequest;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.misc.Executor;
import org.epics.pvdata.misc.ExecutorNode;
import org.epics.pvdata.misc.ThreadPriority;
import org.epics.pvdata.misc.Timer;
import org.epics.pvdata.misc.TimerFactory;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Requester;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.Structure;


/**
 * @author mrk
 *
 */
public class ChannelListFactory {
    
    public static void init(Display display) {
        ChannelListImpl channelListImpl = new ChannelListImpl(display);
        channelListImpl.start();
    }
    private static final PVDataCreate pvDataCreate = PVDataFactory.getPVDataCreate();
    private static final ChannelAccess channelAccess = ChannelAccessFactory.getChannelAccess();
    private static final Executor executor = SwtshellFactory.getExecutor();
    private static final Timer timer = TimerFactory.create("channelListFactory", ThreadPriority.lowest);
    
    private static class ChannelListImpl implements DisposeListener,SelectionListener,Runnable,Timer.TimerCallback
    {
        private ChannelListImpl(Display display) {
            this.display = display;
        }
        
        private boolean isDisposed = false;
        private static String windowName = "channelList";
        private Display display;
        private Shell shell = null;
        private Requester requester = null;
        private Combo providerCombo = null;
        private Text iocnameText = null;
        private Text regularExpressionText = null;
        private Text consoleText = null; 
        private ExecutorNode executorNode = executor.createNode(this);
        private Timer.TimerNode timerNode = TimerFactory.createNode(this);
        private GetChannelField getChannelField = null;
        
        private void start() {
            shell = new Shell(display);
            shell.setText(windowName);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            shell.setLayout(gridLayout);
            Composite provider = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            provider.setLayout(gridLayout);
            new Label(provider,SWT.RIGHT).setText("provider");
            providerCombo = new Combo(provider,SWT.SINGLE|SWT.BORDER);
            String[] names = channelAccess.getProviderNames();
            int pvAcccesInd = 0;
            for(int i=0; i<names.length; i++) {
                if(names[i].equals("pvAccess")) {
                    pvAcccesInd = i;
                    break;
                }
            }
            for(String name :names) {
                providerCombo.add(name);
            }
            providerCombo.select(pvAcccesInd);
            
            Composite composite = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            composite.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            composite.setLayoutData(gridData);   
            new Label(composite,SWT.RIGHT).setText("iocname");
            iocnameText = new Text(composite,SWT.BORDER);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.minimumWidth = 100;
            iocnameText.setLayoutData(gridData);
            
            
            composite = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            composite.setLayout(gridLayout);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            composite.setLayoutData(gridData);   
            new Label(composite,SWT.RIGHT).setText("regularExpression");
            regularExpressionText = new Text(composite,SWT.BORDER);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.minimumWidth = 300;
            regularExpressionText.setLayoutData(gridData);
            regularExpressionText.addSelectionListener(this);
            regularExpressionText.setText(".*");
            
            composite = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            composite.setLayout(gridLayout);
            gridData = new GridData(GridData.FILL_BOTH);
            composite.setLayoutData(gridData);
            Button clearItem = new Button(composite,SWT.PUSH);
            clearItem.setText("&Clear");
            clearItem.addSelectionListener(new SelectionListener() {
                public void widgetDefaultSelected(SelectionEvent arg0) {
                    widgetSelected(arg0);
                }
                public void widgetSelected(SelectionEvent arg0) {
                    consoleText.selectAll();
                    consoleText.clearSelection();
                    consoleText.setText("");
                }
            });
            consoleText = new Text(composite,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.READ_ONLY);
            gridData = new GridData(GridData.FILL_BOTH);
            gridData.heightHint = 400;
            gridData.widthHint = 400;
            consoleText.setLayoutData(gridData);
            requester = SWTMessageFactory.create(windowName,display,consoleText);
            shell.addDisposeListener(this);
            shell.pack();
            shell.open();
            
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
         */
        @Override
        public void widgetDisposed(DisposeEvent e) {
            isDisposed = true;
            GetChannelField temp = getChannelField;
            if(temp!=null) {
                getChannelField = null;
                temp.destroy();
            }
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            Object object = e.getSource(); 
            if(object==regularExpressionText) {
                String providerName = providerCombo.getText();
                String iocname = iocnameText.getText();
                String regularExpression = regularExpressionText.getText();
                if(getChannelField!=null) {
                    consoleText.append("already active");
                    consoleText.append(String.format("%n"));
                    return;
                }
                getChannelField = new GetChannelField(providerName,iocname,regularExpression);
                executor.execute(executorNode);
            }
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            timer.scheduleAfterDelay(timerNode, 5.0);
            getChannelField.connect();
        }
        /* (non-Javadoc)
         * @see org.epics.pvdata.misc.Timer.TimerCallback#callback()
         */
        @Override
        public void callback() {
            if(getChannelField!=null) getChannelField.destroy();
            if(isDisposed) return;
            display.asyncExec( new Runnable() {
                public void run() {
                    consoleText.append("timeOut");
                    getChannelField = null;
                }

            });
        }
        /* (non-Javadoc)
         * @see org.epics.pvdata.misc.Timer.TimerCallback#timerStopped()
         */
        @Override
        public void timerStopped() {
            if(isDisposed) return;
            GetChannelField temp = getChannelField;
            if(temp!=null) temp.destroy();
            display.asyncExec( new Runnable() {
                public void run() {
                    consoleText.append("timerStopped");
                    getChannelField = null;
                }

            });
        }
        
        private void getDone() {
            if(isDisposed) return;
            display.asyncExec( new Runnable() {
                public void run() {
                    if(getChannelField!=null) {
                        consoleText.append(getChannelField.getResult());
                        getChannelField = null;
                    }
                }

            });
        }

        private class GetChannelField implements ChannelRequester,ChannelPutGetRequester {
            
            GetChannelField(String providerName,String iocname, String regularExpression) {
                super();
                this.providerName = providerName;
                this.iocname = iocname;
                this.regularExpression = regularExpression;
            }
            
            private StringArrayData stringArrayData = new StringArrayData(); 
            private String providerName;
            private String iocname;
            private String regularExpression;
            private Channel channel = null;
            private ChannelPutGet channelPutGet = null;
            private PVStructure pvPutStructure = null;
            private BitSet putBitSet = null;
           
            private PVString pvDatabase = null;
            private PVString pvRegularExpression = null;
            
            
            private String result = null;
            
 
            void connect() {
                String channelName = iocname + "recordListPGRPC";
                ChannelProvider channelProvider = channelAccess.getProvider(providerName);
                channel = channelProvider.createChannel(channelName, this, ChannelProvider.PRIORITY_DEFAULT);
            }
            
            void destroy() {
                channel.destroy();
            }
            
            String getResult() {
                return result;
            }
            
            private void createPutGet() { 
            	CreateRequest createRequest = CreateRequest.create();
                PVStructure pvPutRequest = createRequest.createRequest("record[process=true]putField(argument)getField(result)");
                if(pvPutRequest==null) {
                	message(createRequest.getMessage(), MessageType.error);
                	return;
                }
                channelPutGet = channel.createChannelPutGet(this, pvPutRequest);
            }
            
            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelRequester#channelCreated(Status,org.epics.pvaccess.client.Channel)
             */
            @Override
            public void channelCreated(Status status, Channel channel) {
                if (!status.isOK()) {
                	message(status.toString(), status.isSuccess() ? MessageType.warning : MessageType.error);
                	if (!status.isSuccess()) return;
                }
                this.channel = channel;
            }
            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelRequester#channelStateChange(org.epics.pvaccess.client.Channel, org.epics.pvaccess.client.Channel.ConnectionState)
             */
            @Override
            public void channelStateChange(Channel channel, ConnectionState state) {
                if(state == ConnectionState.CONNECTED) {
                    this.channel = channel;
                    createPutGet();
                }
            }

            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelPutGetRequester#channelPutGetConnect(org.epics.pvdata.pv.Status, org.epics.pvaccess.client.ChannelPutGet, org.epics.pvdata.pv.Structure, org.epics.pvdata.pv.Structure)
             */
            @Override
            public void channelPutGetConnect(Status status,
                    ChannelPutGet channelPutGet, Structure putStructure,
                    Structure getStructure)
            {
                if (!status.isOK()) {
                	message(status.toString(), status.isSuccess() ? MessageType.warning : MessageType.error);
                	if (!status.isSuccess()) return;
                }
                this.channelPutGet = channelPutGet;
                
                pvPutStructure = pvDataCreate.createPVStructure(putStructure);
                putBitSet = new BitSet(pvPutStructure.getNumberFields());
                if(pvPutStructure!=null) {
                    pvDatabase = pvPutStructure.getStringField("argument.database");
                    pvRegularExpression = pvPutStructure.getStringField("argument.regularExpression");
                    
                    if(pvDatabase!=null && pvRegularExpression!=null) {
                        putBitSet.clear();
                        pvDatabase.put("master");
                        pvRegularExpression.put(regularExpression);
                        putBitSet.set(0);
                        this.channelPutGet.putGet(pvPutStructure,putBitSet);
                        return;
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("createPutGet failed");
                channel.destroy();
                requester.message(stringBuilder.toString(), MessageType.error);
            }

            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelPutGetRequester#getGetDone(org.epics.pvdata.pv.Status, org.epics.pvaccess.client.ChannelPutGet, org.epics.pvdata.pv.PVStructure, org.epics.pvdata.misc.BitSet)
             */
            @Override
            public void getGetDone(Status status, ChannelPutGet channelPutGet,
                    PVStructure getPVStructure, BitSet getBitSet)
            {}

            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelPutGetRequester#getPutDone(org.epics.pvdata.pv.Status, org.epics.pvaccess.client.ChannelPutGet, org.epics.pvdata.pv.PVStructure, org.epics.pvdata.misc.BitSet)
             */
            @Override
            public void getPutDone(Status status, ChannelPutGet channelPutGet,
                    PVStructure putPVStructure, BitSet putBitSet)
            { }

            /* (non-Javadoc)
             * @see org.epics.pvaccess.client.ChannelPutGetRequester#putGetDone(org.epics.pvdata.pv.Status, org.epics.pvaccess.client.ChannelPutGet, org.epics.pvdata.pv.PVStructure, org.epics.pvdata.misc.BitSet)
             */
            @Override
            public void putGetDone(Status status, ChannelPutGet channelPutGet,
                    PVStructure getPVStructure, BitSet getBitSet)
            {
                if (!status.isOK()) {
                	message(status.toString(), status.isSuccess() ? MessageType.warning : MessageType.error);
                	if (!status.isSuccess()) return;
                }
                PVString pvStatus = getPVStructure.getStringField("result.status");
                if(pvStatus!=null) {
                    String stat = pvStatus.get();
                    if(!stat.isEmpty()) {
                        requester.message(stat,MessageType.warning);
                    }
                }
                PVStringArray pvRecordNames = getPVStructure.getSubField(PVStringArray.class,"result.names");
                if(pvRecordNames==null) {
                    requester.message("result does not have field names",MessageType.error);
                    return;
                }
                
                int length = pvRecordNames.getLength();
                if(length<1) {
                    requester.message("number names <1",MessageType.error);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                pvRecordNames.get(0, length, stringArrayData);
                String[] names = stringArrayData.data;
                for(int i=0; i<length; i++) {
                    stringBuilder.append(names[i]);
                    stringBuilder.append(String.format("%n"));
                }
                result = stringBuilder.toString();
                timerNode.cancel();
                getDone();
                channel.destroy();
            }
            /* (non-Javadoc)
             * @see org.epics.pvdata.pv.Requester#getRequesterName()
             */
            @Override
            public String getRequesterName() {
                return requester.getRequesterName();
            }

            /* (non-Javadoc)
             * @see org.epics.pvdata.pv.Requester#message(java.lang.String, org.epics.pvdata.pv.MessageType)
             */
            @Override
            public void message(String message, MessageType messageType) {
                requester.message(message, MessageType.info);
            }
        }
    }
}
