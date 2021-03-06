/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venefica.common;

import com.venefica.model.UserType;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author gyuszi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DumpErrorTestExecutionListener.class})
@ActiveProfiles("test")
@ContextConfiguration(locations = {"/common-context.xml"})
public class MailChimpSenderTest {
    
    @Inject
    private MailChimpSender mailChimpSender;
    
    @Test
    @Ignore
    public void listSubscribeTest() throws MailException {
        String emailAddress = "venefica.labs@yatko.com";
        //String emailAddress = "venefica.labs@yatko.com";
        Map<String, Object> vars = new HashMap<String, Object>(0);
        vars.put("INVITATION", "1234");
        vars.put("ZIPCODE", "00501");
        vars.put("SOURCE", "Other");
        vars.put("USERTYPE", UserType.GIVER.getDescription());
        
        mailChimpSender.listSubscribe(emailAddress, vars);
    }
    
}
