package org.tmt.addons.ospl.osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataWriterHelper
{

    public static org.tmt.addons.ospl.osplData.MsgDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataWriter) {
            return (org.tmt.addons.ospl.osplData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static org.tmt.addons.ospl.osplData.MsgDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataWriter) {
            return (org.tmt.addons.ospl.osplData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
