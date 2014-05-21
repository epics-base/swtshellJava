/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;


import org.eclipse.swt.widgets.Shell;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.Structure;


public class CreateStructureFactory {
    
    
    public static Structure create(Shell parent) {
        CreateSubFields createSubFields = CreateSubFieldsFactory.create(parent);
        if(!createSubFields.create("create subfields of structure") ) return null;
        Structure structure = FieldFactory.getFieldCreate().createStructure(createSubFields.getFieldNames(),createSubFields.getFields());
        return structure;
    }
   
}
