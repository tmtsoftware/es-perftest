package osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataWriterHelper
{

    public static osplData.MsgDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataWriter) {
            return (osplData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static osplData.MsgDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataWriter) {
            return (osplData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
