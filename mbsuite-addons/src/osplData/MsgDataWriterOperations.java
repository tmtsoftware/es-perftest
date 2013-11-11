package osplData;

public interface MsgDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            osplData.Msg instance_data);

    long register_instance_w_timestamp(
            osplData.Msg instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            osplData.Msg instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            osplData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            osplData.Msg instance_data, 
            long handle);

    int write_w_timestamp(
            osplData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            osplData.Msg instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            osplData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            osplData.Msg instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            osplData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            osplData.MsgHolder key_holder, 
            long handle);
    
    long lookup_instance(
            osplData.Msg instance_data);

}
