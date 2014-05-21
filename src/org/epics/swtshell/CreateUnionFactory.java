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
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.Union;

/**
 * Factory which implements CreateField
 * @author mrk
 *
 */
public class CreateUnionFactory {
  
    public static CreateUnion create(Shell parent) {
        return new CreatUnionImpl(parent);
    }
    private static class CreatUnionImpl extends Dialog implements CreateUnion, SelectionListener {
        private static final FieldCreate fieldCreate = FieldFactory.getFieldCreate();
        private Shell parent = null;
        private Shell shell = null;
        private Button doneButton = null;
        private List typeList = null;
        private int indFieldSelected = -1;

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
            doneButton = new Button(shell,SWT.PUSH);
            doneButton.setText("Done");
            doneButton.addSelectionListener(this);
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
            if(indFieldSelected==-1) return null;
            if(indFieldSelected==0) return createVariant();
            CreateSubFields createSubFields = CreateSubFieldsFactory.create(parent.getShell());
            if(!createSubFields.create(prompt)) return null;
            return fieldCreate.createUnion(createSubFields.getFieldNames(),createSubFields.getFields());
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