package org.tmt.addons.ospl.osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgTypeSupportHelper
{

    public static org.tmt.addons.ospl.osplData.MsgTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgTypeSupport) {
            return (org.tmt.addons.ospl.osplData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static org.tmt.addons.ospl.osplData.MsgTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.tmt.addons.ospl.osplData.MsgTypeSupport) {
            return (org.tmt.addons.ospl.osplData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
