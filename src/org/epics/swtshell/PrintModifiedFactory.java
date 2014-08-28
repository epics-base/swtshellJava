/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

import java.util.Date;

import org.eclipse.swt.widgets.Text;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.property.Alarm;
import org.epics.pvdata.property.AlarmSeverity;
import org.epics.pvdata.property.AlarmStatus;
import org.epics.pvdata.property.PVAlarm;
import org.epics.pvdata.property.PVAlarmFactory;
import org.epics.pvdata.property.PVEnumerated;
import org.epics.pvdata.property.PVEnumeratedFactory;
import org.epics.pvdata.property.PVTimeStamp;
import org.epics.pvdata.property.PVTimeStampFactory;
import org.epics.pvdata.property.TimeStamp;
import org.epics.pvdata.property.TimeStampFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Type;


/**
 * Factory which implements PrintModified.
 * @author mrk
 *
 */
public class PrintModifiedFactory {

    /**
     * Create a PrintModified.
     * @param structureName The structure name.
     * @param pvStructure The structure holding data to print.
     * @param changeBitSet The bitset that shows which fields have been changed.
     * @param overrunBitSet The bitset that shows which fields have been changed multiple times.
     * @param text The text widget in which the output will be printed.
     * @return The PrintModified interface.
     */
    public static PrintModified create(String structureName,Text text) {
        return new PrintModifiedImpl(structureName,text);
    }

    private static class PrintModifiedImpl implements PrintModified{
        private static final Convert convert = ConvertFactory.getConvert();
        private String structureName;
        private Text text;
        
        private StringBuilder builder = new StringBuilder();
        private TimeStamp timeStamp = TimeStampFactory.create();
        private PVTimeStamp pvTimeStamp = PVTimeStampFactory.create();
        private Alarm alarm = new Alarm();
        private PVAlarm pvAlarm = PVAlarmFactory.create();
        private PVEnumerated pvEnumerated = PVEnumeratedFactory.create();
        private PVStructure pvStructure = null;
        private BitSet changeBitSet = null;
        private BitSet overrunBitSet = null;

        private PrintModifiedImpl(String structureName,Text text) {
            this.structureName = structureName;

            this.text = text;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.swtshell.PrintModified#print()
         */
        @Override
        public void print(PVStructure pvStructure,BitSet changeBitSet,BitSet overrunBitSet) {
            builder.setLength(0);
            builder.append(structureName); 

            int offsetChange = changeBitSet.nextSetBit(0);
            int offsetOverrun = overrunBitSet.nextSetBit(0);
            if(offsetChange<0 &&  offsetOverrun<0 ) {
                builder.append(" no changes");
            } else {
//                boolean allChange = ((offsetChange==0) ? true : false);
//                boolean allOverrun = ((offsetOverrun==0) ? true : false);
                printStructure(pvStructure,changeBitSet,overrunBitSet,0,false,false);
            }
            convert.newLine(builder, 0);
            text.append(builder.toString());
        }


        @Override
        public void setArgs(PVStructure pvStructure, BitSet changeBitSet, BitSet overrunBitSet)
        {
            this.pvStructure = pvStructure;
            this.changeBitSet = changeBitSet;
            if(overrunBitSet==null) overrunBitSet = new BitSet(changeBitSet.size());
            this.overrunBitSet = overrunBitSet;
        }
        @Override
        public void print() { 
            if(pvStructure==null) {
                throw new IllegalArgumentException("setArgs not called first");
            }
            print(pvStructure,changeBitSet,overrunBitSet);
            pvStructure = null;
            changeBitSet = null;
            overrunBitSet = null;

        }
        private void printStructure(
            PVStructure pvStructure,
            BitSet changeBitSet,
            BitSet overrunBitSet,
            int indentLevel,
            boolean printAll,
            boolean overrunAll)
        {
            int offset = pvStructure.getFieldOffset();
            if(changeBitSet.get(offset)) printAll = true;
            if(overrunBitSet.get(offset)) overrunAll = true;
            String fieldName = pvStructure.getFieldName();
            String id = pvStructure.getStructure().getID();
            if(fieldName!=null && fieldName.equals("timeStamp") && pvTimeStamp.attach(pvStructure)) {
                convert.newLine(builder, indentLevel);
                pvTimeStamp.get(timeStamp);
                long milliPastEpoch = timeStamp.getMilliSeconds();
                int userTag = timeStamp.getUserTag();
                Date date = new Date(milliPastEpoch);
                builder.append(String.format("%s %tF %tT.%tL userTag %d", id,date,date,date,userTag));
                if(overrunAll || overrunBitSet.get(offset)) {
                    builder.append(" overrun");
                }
                return;
            }
            if(indentLevel>0) {
                convert.newLine(builder, indentLevel);
                builder.append(id + " " + fieldName);
            }
            if(fieldName!=null && pvStructure.getFieldName().equals("alarm") && pvAlarm.attach(pvStructure)) {
                pvAlarm.get(alarm);
                PVField[] pvFields = pvStructure.getPVFields();
                if(printAll || changeBitSet.get(pvFields[0].getFieldOffset())) {
                    convert.newLine(builder, indentLevel+1);
                    builder.append("severity ");
                    AlarmSeverity severity = alarm.getSeverity();
                    builder.append(severity.toString());
                    builder.append(" status ");
                    AlarmStatus status = alarm.getStatus();
                    builder.append(status.toString());
                    if(overrunAll || overrunBitSet.get(pvFields[0].getFieldOffset())) {
                        builder.append(" overrun");
                    }
                }
                if(printAll || changeBitSet.get(pvFields[0].getFieldOffset())) {
                    convert.newLine(builder, indentLevel+1);
                    builder.append("message ");
                    String message = alarm.getMessage();
                    builder.append(message);
                    if(overrunAll || overrunBitSet.get(pvFields[1].getFieldOffset())) {
                        builder.append(" overrun");
                    }
                }
                return;
            }
            PVField[] pvFields = pvStructure.getPVFields();
            for(PVField pvField : pvFields) {
                offset = pvField.getFieldOffset();
                if(pvField.getField().getType()==Type.structure) {
                    boolean printIt = false;
                    int nextSet = changeBitSet.nextSetBit(offset);
                    if(nextSet>=0 && (nextSet<pvField.getNextFieldOffset())) printIt = true;
                    if(printAll || printIt) {
                        printStructure(
                            (PVStructure)pvField,
                            changeBitSet,
                            overrunBitSet,
                            indentLevel+1,
                            printAll,
                            overrunAll);
                    }
                    continue;
                }
                if(!printAll && !changeBitSet.get(offset)) continue;
                convert.newLine(builder, indentLevel+1);
                pvField.toString(builder, indentLevel+1);
                if(pvField.getFieldName().equals("index") && pvEnumerated.attach(pvField.getParent())) {
                    builder.append(" choice ");
                    builder.append(pvEnumerated.getChoice());
                }
                if(overrunAll || overrunBitSet.get(offset)) {
                    builder.append(" overrun");
                }
            }
        }
    }
}
