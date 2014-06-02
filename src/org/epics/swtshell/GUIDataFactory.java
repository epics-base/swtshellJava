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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.PVUnionArray;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.pvdata.pv.Type;
import org.epics.pvdata.pv.Union;
import org.epics.pvdata.pv.UnionArrayData;

/**
 * Factory which implements GUIData.
 * @author mrk
 *
 */
public class GUIDataFactory {
    /**
     * Create a GUIData and return the interface.
     * @param parent The parent shell.
     * @return The interface.
     */
    public static GUIData create() {
        return new GUIDataImpl();
    }
    private static final Convert convert = ConvertFactory.getConvert();
    private static class GUIDataImpl implements GUIData {
    	private static final PVDataCreate pvDataCreate = PVDataFactory.getPVDataCreate();
        
                
        
        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public GUIDataImpl() {}
        
        private static void textMessage(Text text,String message) {
            text.selectAll();
            text.clearSelection();
            if(message==null) message = "";
            text.setText(message);
        }
        
        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getScalarValue(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVScalar)
         */
        @Override
        public boolean getScalarValue(Shell parent, PVScalar pvScalar) {
            GetScalar getScalar = new GetScalar(parent,pvScalar);
            return getScalar.get();
        }

        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getUnionValue(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVUnion)
         */
        @Override
        public boolean getUnionValue(Shell parent, PVUnion pvUnion) {
            GetUnion getUnion = new GetUnion(parent,pvUnion);
            return getUnion.get();
        }

        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getStructure(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVStructure, org.epics.pvdata.misc.BitSet)
         */
        @Override
        public void getStructure(Shell parent, PVStructure pvStructure,BitSet bitSet) {
            GetStructure getStructure = new GetStructure(parent,pvStructure,bitSet);
            getStructure.get();
        }

        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getScalarArray(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVScalarArray)
         */
        @Override
        public boolean getScalarArray(Shell parent, PVScalarArray pvScalarArray) {
            GetScalarArray getScalarArray = new GetScalarArray(parent,pvScalarArray);
            return getScalarArray.get();
            
        }

        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getStructureArray(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVStructureArray)
         */
        @Override
        public boolean getStructureArray(Shell parent,PVStructureArray pvStructureArray) {
            boolean result = false;
            PVStructure[] pvStructures = new PVStructure[1];
            StructureArrayData structureArrayData = new StructureArrayData();
            while(true) {
                int index = GetIntegerFactory.create(parent).getInteger("arrayIndex");
                if(index<0) break;
                int num = pvStructureArray.get(index, 1, structureArrayData);
                pvStructures[0] = null;
                if(num==1) {
                    pvStructures[0] = structureArrayData.data[structureArrayData.offset];
                }
                if(pvStructures[0]==null) {
                    pvStructures[0] = pvDataCreate.createPVStructure(pvStructureArray.getStructureArray().getStructure());
                }
                PVStructure pvStruct = pvStructures[0];
                GUIData guiData = GUIDataFactory.create();
                BitSet bitSet = new BitSet(pvStruct.getNumberFields());
                guiData.getStructure(parent, pvStruct,bitSet);
                pvStructureArray.put(index, 1, pvStructures, 0);
                result = true;
            }
            return result;
        }

        /* (non-Javadoc)
         * @see org.epics.swtshell.GUIData#getUnionArray(org.eclipse.swt.widgets.Shell, org.epics.pvdata.pv.PVUnionArray)
         */
        @Override
        public boolean getUnionArray(Shell parent, PVUnionArray pvUnionArray) {
            boolean result = false;
            PVUnion[] pvUnions = new PVUnion[1];
            UnionArrayData unionArrayData = new UnionArrayData();
            while(true) {
                int index = GetIntegerFactory.create(parent).getInteger("arrayIndex");
                if(index<0) break;
                int num = pvUnionArray.get(index, 1, unionArrayData);
                pvUnions[0] = null;
                if(num==1) {
                    pvUnions[0] = unionArrayData.data[unionArrayData.offset];
                }
                if(pvUnions[0]==null) {
                    pvUnions[0] = pvDataCreate.createPVUnion(pvUnionArray.getUnionArray().getUnion());
                }
                PVUnion pvStruct = pvUnions[0];
                GUIData guiData = GUIDataFactory.create();
                guiData.getUnionValue(parent, pvStruct);
                pvUnionArray.put(index, 1, pvUnions, 0);
                result = true;
            }
            return result;
        }

        
        private static class GetScalar extends Dialog implements SelectionListener{
            private PVScalar pvScalar;
            private Shell shell;
            private Text text;
            private boolean modified = false;

            private GetScalar(Shell parent,PVScalar pvField) {
                super(parent,SWT.DIALOG_TRIM|SWT.NONE);
                pvScalar = pvField;
            }

            private boolean get() {
                shell = new Shell(super.getParent());
                shell.setText("value");
                GridLayout gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                shell.setLayout(gridLayout);
                text = new Text(shell,SWT.BORDER);
                GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
                gridData.minimumWidth = 100;
                text.setLayoutData(gridData);
                text.addSelectionListener(this);
                textMessage(text,convert.toString(pvScalar));

                shell.pack();
                shell.open();
                Display display = shell.getDisplay();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                shell.dispose();
                return  modified;
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
                        convert.fromString(pvScalar, text.getText());
                        modified = true;
                    }catch (NumberFormatException ex) {
                        textMessage(text,"exception " + ex.getMessage());
                        return;
                    }
                    shell.close();
                    return;
                }
            }

        }
        
        private class GetUnion extends Dialog implements SelectionListener{
            private PVUnion pvUnion;
            private Shell shell;
            private Union union = null;
            private List fieldNameList = null;
            private boolean result = false;
            private boolean firstTime = true;
           

            private GetUnion(Shell parent,PVUnion pvField) {
                super(parent,SWT.DIALOG_TRIM|SWT.NONE);
                pvUnion = pvField;
            }

            private boolean get() {
                union = pvUnion.getUnion();
                if(union.isVariant()) {
                    Field field = CreateFieldFactory.create(super.getParent()).create("create subfield for variant union");
                    if(field==null) return false;
                    PVField pvField = pvDataCreate.createPVField(field);
                    GUIData guiData = GUIDataFactory.create();
                    switch(field.getType()) {
                        case scalar: {
                            if(!guiData.getScalarValue(super.getParent(), (PVScalar)pvField)) return false;
                            pvUnion.set(pvField);
                            return true;
                        }
                        case union: {
                            if(!guiData.getUnionValue(super.getParent(), (PVUnion)pvField)) return false;
                            pvUnion.set(pvField);
                            return true;
                        }
                        case structure: {
                            PVStructure pvStructure = (PVStructure)pvField;
                            BitSet bitSet = new BitSet(pvStructure.getNumberFields());
                            guiData.getStructure(super.getParent(), pvStructure,bitSet);
                            return !bitSet.isEmpty();
                        }
                        case scalarArray:{
                            if(!guiData.getScalarArray(super.getParent(), (PVScalarArray)pvField)) return false;
                            pvUnion.set(pvField);
                            return true;
                        }
                        case structureArray:{
                            if(!guiData.getStructureArray(super.getParent(), (PVStructureArray)pvField)) return false;
                            pvUnion.set(pvField);
                            return true;
                            
                        }
                        case unionArray:{
                            if(!guiData.getUnionArray(super.getParent(), (PVUnionArray)pvField)) return false;
                            pvUnion.set(pvField);
                            return true;
                        }
                    }
                }
                String[] fieldNames = union.getFieldNames();
                shell = new Shell(super.getParent());
                shell.setText("fieldName");
                GridLayout gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                shell.setLayout(gridLayout);
                
                fieldNameList = new List(shell,SWT.SINGLE);
                fieldNameList.addSelectionListener(this);
                for(int i=0; i<fieldNames.length; ++i) fieldNameList.add(fieldNames[i]);
                shell.pack();
                shell.open();
                Display display = shell.getDisplay();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                shell.dispose();
                return result;
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
                if(object==fieldNameList) {
                    if(firstTime) {
                        firstTime = false;
                        return;
                    }
                    int indFieldSelected = fieldNameList.getFocusIndex();
                    Field field = union.getField(indFieldSelected);
                    pvUnion.select(indFieldSelected);
                    if(field==null) return;
                    PVField pvField = pvDataCreate.createPVField(field);
                    GUIData guiData = GUIDataFactory.create();
                    Type type = pvField.getField().getType();
                    switch(type) {
                    case scalar: {
                        if(!guiData.getScalarValue(super.getParent(),(PVScalar)pvField))break;
                        pvUnion.set(pvField);
                        result = true;
                        break;
                    }
                    case union: {
                        if(!guiData.getUnionValue(super.getParent(),(PVUnion)pvField)) break;
                        pvUnion.set(pvField);
                        result = true;
                        break;
                    }
                    case scalarArray: {
                        if(!guiData.getScalarArray(super.getParent(),(PVScalarArray)pvField)) break;
                        pvUnion.set(pvField);
                        result = true;
                        break;
                    }
                    case structure: {
                        PVStructure pvStruct = (PVStructure)pvField;
                        BitSet bitSet = new BitSet(pvStruct.getNumberFields());
                        guiData.getStructure(super.getParent(),pvStruct,bitSet);
                        result = !bitSet.isEmpty();
                        break;
                    }
                    case structureArray: {
                        if(!guiData.getStructureArray(super.getParent(),(PVStructureArray)pvField)) break;
                        pvUnion.set(pvField);
                        result = true;
                        break;
                    }   
                    case unionArray: {
                        if(!guiData.getUnionArray(super.getParent(),(PVUnionArray)pvField)) break;
                        pvUnion.set(pvField);
                        break;
                    }
                    }
                    shell.close();
                }

            }

        }
        
        private static class GetScalarArray extends Dialog implements SelectionListener{
            private PVScalarArray pvScalarArray;
            private Shell shell;
            private Text text;
            private boolean modified = false;
            
            private GetScalarArray(Shell parent,PVScalarArray pvField) {
                super(parent,SWT.DIALOG_TRIM|SWT.NONE);
                pvScalarArray = pvField;
            }
            
            private boolean get() {
                shell = new Shell(super.getParent());
                shell.setText("value");
                GridLayout gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                shell.setLayout(gridLayout);
                text = new Text(shell,SWT.BORDER);
                GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
                gridData.minimumWidth = 100;
                text.setLayoutData(gridData);
                text.addSelectionListener(this);
                String[] values = new String[pvScalarArray.getLength()];
                convert.toStringArray(pvScalarArray, 0, values.length, values, 0);
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0; i<values.length; i++ ) {
                    if(i>0) stringBuilder.append(",");
                    stringBuilder.append(values[i]);
                }
                textMessage(text,stringBuilder.toString());
                shell.pack();
                shell.open();
                Display display = shell.getDisplay();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                shell.dispose();
                return  modified;
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
                        convert.fromString(pvScalarArray, text.getText());
                        modified = true;

                    }catch (NumberFormatException ex) {
                        textMessage(text,"exception " + ex.getMessage());
                        return;
                    }
                    shell.close();
                    return;
                }
            }

        }
        
        private class GetStructure extends Dialog implements SelectionListener{
            private PVStructure pvStructure;
            private Shell parent;
            private BitSet bitSet;
            private PVField[] pvFields;
            private String[] fieldNames;
            private Shell shell = null;
            private Button doneButton = null;
            private List list = null;
            
            private GetStructure(Shell parent,PVStructure pvField,BitSet bitSet) {
                super(parent,SWT.DIALOG_TRIM|SWT.NONE);
                pvStructure = pvField;
                this.parent = parent;
                this.bitSet = bitSet;
                pvFields = pvStructure.getPVFields();
                fieldNames = pvStructure.getStructure().getFieldNames();
            }
            
            private void get() {
                if(pvFields.length<1) return;
                shell = new Shell(parent);
                GridLayout gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                shell.setLayout(gridLayout);
                Composite composite = new Composite(shell,SWT.BORDER);
                gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                composite.setLayout(gridLayout);
                doneButton = new Button(composite,SWT.PUSH);
                doneButton.setText("Done");
                doneButton.addSelectionListener(this);
                //list = new List(composite, SWT.V_SCROLL);
                list = new List(composite,0);
                for (int i=0; i<fieldNames.length; ++i) {
                    list.add(fieldNames[i]);
                }
                int itemHeigth = list.getItemHeight();
                int width = 200;
                int heigth = itemHeigth*(fieldNames.length);
                list.setSize(width,heigth);
                heigth = itemHeigth*(fieldNames.length + 3);
                composite.setSize(width,heigth);
                heigth = itemHeigth*(fieldNames.length + 4);
                shell.setSize(width,heigth);
                list.addSelectionListener(this);
                shell.open();
                Display display = shell.getDisplay();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                shell.dispose();
            }
            
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                Object object = arg0.getSource();
                if(object==doneButton) {
                    shell.close();
                    return;
                }
                int[] selectedItems = list.getSelectionIndices();
                for(int i=0; i< selectedItems.length; ++i)
                {
                    PVField pvf = pvFields[selectedItems[i]];
                    int offset = pvf.getFieldOffset();
                    Field field = pvf.getField();
                    Type type = field.getType();
                    switch(type) {
                    case scalar:
                    {
                       if(getScalarValue(shell,(PVScalar)pvf)) bitSet.set(offset);
                        break;
                    }
                    case union:
                    {
                        if(getUnionValue(shell,(PVUnion)pvf)) bitSet.set(offset);
                        break;
                    }
                    case structure:
                    {
                        getStructure(shell,(PVStructure)pvf, bitSet);
                        break;
                    }
                    case scalarArray:
                    {
                        if(getScalarArray(shell,(PVScalarArray)pvf)) bitSet.set(offset);
                        break;
                    }
                    case structureArray:
                    {
                        if(getStructureArray(shell,(PVStructureArray)pvf)) bitSet.set(offset);
                        break;
                    }
                    case unionArray:
                    {
                        if(getUnionArray(shell,(PVUnionArray)pvf)) bitSet.set(offset);
                        break;
                    }
                    }
                }
            }
        }                      
    }
}
