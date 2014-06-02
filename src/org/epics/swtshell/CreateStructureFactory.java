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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.Structure;


/**
 * @author mrk
 * 
 * Factory for creating a Structure
 *
 */
public class CreateStructureFactory {
    /**
     * 
     * Create a CreateStructure.
     * @param parent The parent shell.
     * @return the interface.
     */
    public static CreateStructure create(Shell parent) {
        return new CreateStructureImpl(parent);
    }
    
    private static class CreateStructureImpl extends Dialog implements CreateStructure, SelectionListener {
       
        private Shell parent;
        private Shell shell = null;
        private Button button = null;
        private Structure structure = null;

        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public CreateStructureImpl(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.parent = parent;
        }

        @Override
        public Structure create(String prompt) {
            shell = new Shell(parent);
            shell.setText(prompt);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            GridData shellData = new GridData();
            shellData.minimumWidth = 200;
            shellData.grabExcessHorizontalSpace = true;
            shell.setLayout(gridLayout);
            shell.setLayoutData(shellData);
            new Label(shell,SWT.NONE).setText("createStructure");
            button = new Button(shell,SWT.PUSH);
            button.setText("create");
            button.addSelectionListener(this);
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            return structure;
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
            widgetSelected(arg0);
        }

        @Override
        public void widgetSelected(SelectionEvent arg0) {
            Object object = arg0.getSource();
            if(object==button) {
                CreateSubFields createSubFields = CreateSubFieldsFactory.create(shell);
                if(createSubFields.create("create subfields of structure") ) {
                    structure = FieldFactory.getFieldCreate().createStructure(createSubFields.getFieldNames(),createSubFields.getFields());
                }
                shell.close();
            }
        }
    }
}
