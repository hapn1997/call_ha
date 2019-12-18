package vn.com.call.ui.main;

import com.chad.library.adapter.base.entity.SectionEntity;

import vn.com.call.model.calllog.CallLog;

/**
 * Created by ngson on 04/07/2017.
 */

public class CallLogSectionEntity extends SectionEntity<CallLog> {
    public CallLogSectionEntity(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public CallLogSectionEntity(CallLog callLog) {
        super(callLog);
    }
}
