package org.tmt.addons.ospl.osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderViewHelper
{

    public static org.tmt.addons.ospl.osplData.MsgDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataReaderView) {
            return (org.tmt.addons.ospl.osplData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static org.tmt.addons.ospl.osplData.MsgDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgDataReaderView) {
            return (org.tmt.addons.ospl.osplData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
