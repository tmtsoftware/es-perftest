package osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgTypeSupportHelper
{

    public static osplData.MsgTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgTypeSupport) {
            return (osplData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static osplData.MsgTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgTypeSupport) {
            return (osplData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
