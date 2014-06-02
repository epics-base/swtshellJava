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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.Union;

/**
 * Factory which implements CreateUnion
 * @author mrk
 *
 */
public class CreateUnionFactory {
  
    /**
     * Create a CreateUnion.
     * @param parent the parent shell
     * @return The interface.
     */
    public static CreateUnion create(Shell parent) {
        return new CreatUnionImpl(parent);
    }
    private static class CreatUnionImpl extends Dialog implements CreateUnion, SelectionListener {
        private static final FieldCreate fieldCreate = FieldFactory.getFieldCreate();
        private Shell parent = null;
        private Shell shell = null;
        private List typeList = null;
        private Union union = null;
        private boolean firstTime = true;

        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public CreatUnionImpl(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.parent = parent;
        }
        @Override
        public Union createVariant() {
            return fieldCreate.createVariantUnion();
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.swtshell.CreateStructure#create()
         */
        @Override
        public Union create(String prompt) {
            shell = new Shell(parent);
            shell.setText(prompt);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            GridData shellData = new GridData();
            shellData.widthHint = 100;
            shellData.grabExcessHorizontalSpace = true;
            shell.setLayout(gridLayout);
            shell.setLayoutData(shellData);
            typeList = new List(shell,SWT.BORDER | SWT.H_SCROLL);
            typeList.addSelectionListener(this);
            GridData typeListData = new GridData();
            typeListData.minimumWidth = 200;
            typeListData.grabExcessHorizontalSpace = true;
            typeList.setLayoutData(typeListData);
            typeList.add("variant union");
            typeList.add("regular union");
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            return union;
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
            
            if(object==typeList) {
                if(firstTime) {
                    firstTime = false;
                    return;
                }
                int indSelected = typeList.getFocusIndex();
                if(indSelected==0) {
                    union = fieldCreate.createVariantUnion();
                }
                if(indSelected==1) {
                    CreateSubFields createSubFields = CreateSubFieldsFactory.create(shell);
                    if(createSubFields.create("variantFields")) {
                        union = fieldCreate.createUnion(createSubFields.getFieldNames(),createSubFields.getFields());
                    }
                }
                shell.close();
                return;
            }
        }
    }
}