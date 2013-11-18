package org.tmt.addons.ospl.osplData;

public interface MsgDataReaderViewOperations extends
    DDS.DataReaderViewOperations
{

    int read(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_w_condition(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int take_w_condition(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int read_next_sample(
            org.tmt.addons.ospl.osplData.MsgHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int take_next_sample(
            org.tmt.addons.ospl.osplData.MsgHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int read_instance(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples,
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_instance(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_next_instance(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance_w_condition(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int take_next_instance_w_condition(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int return_loan(
            org.tmt.addons.ospl.osplData.MsgSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq);

    int get_key_value(
            org.tmt.addons.ospl.osplData.MsgHolder key_holder, 
            long handle);
    
    long lookup_instance(
            org.tmt.addons.ospl.osplData.Msg instance);

}
