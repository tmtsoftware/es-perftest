package osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderHelper
{

    public static osplData.MsgDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataReader) {
            return (osplData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static osplData.MsgDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataReader) {
            return (osplData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
