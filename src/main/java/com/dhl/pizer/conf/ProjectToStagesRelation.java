package com.dhl.pizer.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectToStagesRelation {

    public static Map<String, List<String>> projectToStagesMap = new HashMap<String, List<String>>() {{
        put( "阿斯利康-传感器触发" , new ArrayList<String>() {{
            add( "PP_TO_TAKELEADINGPOINT" );
            add( "PICKUPPOINT_TO_PLUGBOARDTEST" );
//            add( "PLUGBOARDTEST_TO_TAKEPOINT" );
            add( "TAKEPOINT_TO_DISCHARGELEADINGPOINT" );
            add( "DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT" );
        }});
        put( "阿斯利康-手持端" , new ArrayList<String>() {{
            add( "MOBILE_PP_TO_TAKELEADINGPOINT" );
            add( "MOBILE_TAKELEADINGPOINT_TO_TAKEPOINT" );
            add( "MOBILE_TAKEPOINT_TO_DISCHARGELEADINGPOINT" );
            add( "MOBILE_DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT" );
        }});
    }};

}
