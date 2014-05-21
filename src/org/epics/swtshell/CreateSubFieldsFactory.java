/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;


import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.pvdata.pv.Field;

/**
 * Factory which implements CreateField
 * @author mrk
 *
 */
public class CreateSubFieldsFactory {
    /**
     * Create a CDGet and return the interface.
     * @param parent The parent shell.
     * @return The CDGet interface.
     */
    public static CreateSubFields create(Shell parent) {
        return new CreateSubFieldsImpl(parent);
    }
    private static class CreateSubFieldsImpl extends Dialog implements CreateSubFields, SelectionListener {
        private static final Pattern commaPattern = Pattern.compile("[,]");
        private Shell parent = null;
        private Shell shell = null;
        private Text namesText = null;
        private String[] fieldNames = null;
        private Field[] fields = null;
        private String message = null;
        /**
         * Constructor.
         * @param parent The parent shell.
         */
        public CreateSubFieldsImpl(Shell parent) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.parent = parent;
        }
        @Override
        public String[] getFieldNames() {
            return fieldNames;
        }
        @Override
        public Field[] getFields() {
            return fields;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.swtshell.CreateStructure#create()
         */
        @Override
        public boolean create(String prompt) { 
            shell = new Shell(parent);
            
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            GridData shellData = new GridData();
            shellData.minimumWidth = 800;
            shellData.grabExcessHorizontalSpace = true;
            shell.setText(prompt);
            shell.setLayout(gridLayout);
            shell.setLayoutData(shellData);
            new Label(shell,SWT.LEFT).setText("enter field names");
            namesText = new Text(shell,SWT.SINGLE);
            namesText.addSelectionListener(this);
            namesText.setMessage("                                                             ");
            GridData textData = new GridData();
            textData.minimumWidth = 100;
            namesText.setLayoutData(textData);
            
            shell.pack();
            shell.open();
            Display display = shell.getDisplay();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
            if(message==null) return false;
            if(message.length()<1) return false;
            fieldNames  = commaPattern.split(message);
            int number = fieldNames.length;
            if(number<1) return false;
            fields = new Field[number];
            boolean allOK = true;
            for(int i=0; i <number; ++i) {
                fields[i] = CreateFieldFactory.create(parent).create("create field " + fieldNames[i]);
                if(fields[i]==null) allOK = false;
            }
            return allOK;
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
            if(object==namesText) {
                message = namesText.getText();
                shell.close();
            }
        }

    }

    
}