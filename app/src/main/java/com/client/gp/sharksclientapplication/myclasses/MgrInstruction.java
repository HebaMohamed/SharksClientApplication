package com.client.gp.sharksclientapplication.myclasses;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dell on 2/14/2017.
 */

public class MgrInstruction {
    public String msg;
    public Long timestamp;
    public MgrInstruction(String msg, Long timestamp){
        this.msg=msg;
        this.timestamp=timestamp;
    }
    public String getInstructionDateTimeString(){
        Format sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date netDate = (new Date(timestamp));
        String s = sdf.format(netDate);
        return s;
    }
}
