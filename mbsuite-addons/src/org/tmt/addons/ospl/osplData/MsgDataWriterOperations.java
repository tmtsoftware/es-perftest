package org.tmt.addons.ospl.osplData;

public interface MsgDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            org.tmt.addons.ospl.osplData.Msg instance_data);

    long register_instance_w_timestamp(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long handle);

    int write_w_timestamp(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            org.tmt.addons.ospl.osplData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            org.tmt.addons.ospl.osplData.MsgHolder key_holder, 
            long handle);
    
    long lookup_instance(
            org.tmt.addons.ospl.osplData.Msg instance_data);

}
