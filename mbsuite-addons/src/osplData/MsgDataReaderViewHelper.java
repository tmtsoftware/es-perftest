package osplData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderViewHelper
{

    public static osplData.MsgDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataReaderView) {
            return (osplData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static osplData.MsgDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof osplData.MsgDataReaderView) {
            return (osplData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
