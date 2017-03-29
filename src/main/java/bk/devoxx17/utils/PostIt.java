package bk.devoxx17.utils;

import org.apache.log4j.Logger;

import javax.ws.rs.POST;

public class PostIt {
    private static final Logger log = Logger.getLogger(PostIt.class);

    public PostIt(){
        log.info("don't leave your password on a posit on your desk");
    }

}
