package org.tmt.addons.ospl.osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderHelper
{

    public static org.tmt.addons.ospl.osplData.MsgDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataReader) {
            return (org.tmt.addons.ospl.osplData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static org.tmt.addons.ospl.osplData.MsgDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataReader) {
            return (org.tmt.addons.ospl.osplData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
