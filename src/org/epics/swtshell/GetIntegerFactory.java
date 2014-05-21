/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Factory which implements CDGet.
 * @author mrk
 *
 */
public class GetIntegerFactory {
    /**
     * Create a CDGet and return the interface.
     * @param parent The parent shell.
     * @return The CDGet interface.
     */
    public static GetInteger create(Shell parent) {
        return new GetNumberOfImpl(parent);
    }
    
    private static class GetNumberOfImpl extends Dialog implements GetInteger, SelectionListener {
        private Shell shell = null;
        private Text text;
        private int index=0;
        
        private static void textMessage(Text text,String message) {
            text.selectAll();
            text.clearSelection();
            if(message==null) message = "";
            text.setText(message);
        }
        
        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public GetNumberOfImpl(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.swtshell.CreateStructure#create()
         */
        @Override
        public int getInteger(String prompt) {
            shell = new Shell(super.getParent());
            shell.setText(prompt);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            shell.setLayout(gridLayout);
            text = new Text(shell,SWT.BORDER);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.minimumWidth = 200;
            text.setLayoutData(gridData);
            text.addSelectionListener(this);
            textMessage(text,String.valueOf(index));
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            return index;
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
            if(object==text) {  
                try {
                    index = Integer.parseInt(text.getText());
                }catch (NumberFormatException ex) {
                    textMessage(text,"exception " + ex.getMessage());
                    return;
                }
                shell.close();
                return;
            }
        }
    }
}
