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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.Type;
import org.epics.pvdata.pv.Union;

/**
 * Factory which implements CreateField
 * @author mrk
 *
 */
public class CreateFieldFactory {
    /**
     * Create a CDGet and return the interface.
     * @param parent The parent shell.
     * @return The CDGet interface.
     */
    public static CreateField create(Shell parent) {
        return new CreateFieldImpl(parent);
    }
    private static class CreateFieldImpl extends Dialog implements CreateField, SelectionListener {
        private static final FieldCreate fieldCreate = FieldFactory.getFieldCreate();
        private Shell parent = null;
        private Shell shell = null;
        private List typeList = null;
        private Button doneButton = null;
        private int indFieldSelected = -1;

        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public CreateFieldImpl(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.parent = parent;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.swtshell.CreateStructure#create()
         */
        @Override
        public Field create(String prompt) {
            shell = new Shell(parent);
            shell.setText(prompt);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            GridData shellData = new GridData();
            shellData.minimumWidth = 200;
            shellData.grabExcessHorizontalSpace = true;
            shell.setLayout(gridLayout);
            shell.setLayoutData(shellData);
            doneButton = new Button(shell,SWT.PUSH);
            doneButton.setText("Done");
            doneButton.addSelectionListener(this);
            String[] typeNames = new String[] {
                    "scalar","scalarArray","structure","structureArray","union","unionArray"
            };

            typeList = new List(shell,SWT.BORDER | SWT.H_SCROLL);
            typeList.addSelectionListener(this);
            GridData typeListData = new GridData();
            typeListData.minimumWidth = 200;
            typeListData.grabExcessHorizontalSpace = true;
            typeList.setLayoutData(typeListData);
            for(int i=0; i<typeNames.length; ++i) typeList.add(typeNames[i]);
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            if(indFieldSelected==-1) return null;
            Type type = Type.valueOf(typeNames[indFieldSelected]);
            switch(type) {
            case scalar:
            {
                ScalarType scalarType = new GetScalarType(super.getParent()).get("scalarType");
                if(scalarType==null) return null;
                return fieldCreate.createScalar(scalarType);
                
            }
            case union:
            {
                return CreateUnionFactory.create(super.getParent()).create("create union");
            }
            case scalarArray:
            {
                ScalarType scalarType = new GetScalarType(super.getParent()).get("scalarType");
                if(scalarType==null) return null;
                return fieldCreate.createScalarArray(scalarType);
            }
            case structure:
            {
                Structure structure = CreateStructureFactory.create(super.getParent());
                if(structure==null) return null;
                return fieldCreate.createStructure(structure);
            }
            case structureArray:
            {
                Structure structure = CreateStructureFactory.create(super.getParent());
                if(structure==null) return null;
                return fieldCreate.createStructureArray(structure);
            }

            case unionArray:
            {
                Union union = CreateUnionFactory.create(super.getParent()).create("create union");
                if(union==null) return null;
                return fieldCreate.createUnionArray(union);
            }
            }
            return null;
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
            if(object==doneButton) {
                shell.close();
                return;
            }
            if(object==typeList) {
                indFieldSelected = typeList.getFocusIndex();
                return;
            }
        }

    }

    private static class GetScalarType extends Dialog implements  SelectionListener {
        private Shell parent = null;
        private Shell shell = null;
        private Button doneButton = null;
        private List typeList = null;
        private int indFieldSelected = -1;

        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public GetScalarType(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.parent = parent;
        }

        public ScalarType get(String prompt) {
            shell = new Shell(parent);
            shell.setText(prompt);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            GridData shellData = new GridData();
            shellData.widthHint = 300;
            shellData.grabExcessHorizontalSpace = true;
            shell.setLayout(gridLayout);
            shell.setLayoutData(shellData);
            doneButton = new Button(shell,SWT.PUSH);
            doneButton.setText("Done");
            doneButton.addSelectionListener(this);

            String[] typeNames = new String[] {
                    "boolean","byte","short","int","long",
                    "ubyte","ushort","uint","ulong",
                    "float","double","string"
            };

                        typeList = new List(shell,SWT.BORDER | SWT.H_SCROLL);
            typeList.addSelectionListener(this);
            GridData typeListData = new GridData();
            typeListData.minimumWidth = 300;
            typeListData.grabExcessHorizontalSpace = true;
            typeList.setLayoutData(typeListData);
            for(int i=0; i<typeNames.length; ++i) typeList.add(typeNames[i]);
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            if(indFieldSelected==-1) return null;
            return ScalarType.getScalarType(typeNames[indFieldSelected]);
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
            if(object==doneButton) {
                shell.close();
                return;
            }
            if(object==typeList) {
                indFieldSelected = typeList.getFocusIndex();
                return;
            }

        }
    }
}