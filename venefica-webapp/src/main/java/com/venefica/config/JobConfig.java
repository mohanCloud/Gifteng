/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venefica.config;

import com.venefica.common.EmailSender;
import com.venefica.dao.AdDao;
import com.venefica.dao.ForgotPasswordDao;
import com.venefica.dao.InvitationDao;
import com.venefica.dao.UserDao;
import com.venefica.dao.UserVerificationDao;
import com.venefica.job.AdExpirationJob;
import com.venefica.job.AdOnlineJob;
import com.venefica.job.ForgotPasswordExpirationJob;
import com.venefica.job.InvitationExpirationJob;
import com.venefica.job.InvitationReminderJob;
import com.venefica.job.UserVerificationReminderJob;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import org.quartz.Job;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 *
 * @author gyuszi
 */
@Configuration
@PropertySource("/application.properties")
public class JobConfig {
    
    private static final String AD_EXPIRATION_JOB_KEY = "adExpirationJob";
    private static final String AD_EXPIRATION_TRIGGER_KEY = "adExpirationTrigger";
    
    private static final String AD_ONLINE_JOB_KEY = "adOnlineJob";
    private static final String AD_ONLINE_TRIGGER_KEY = "adOnlineTrigger";
    
    private static final String INVITATION_JOB_KEY = "invitationExpirationJob";
    private static final String INVITATION_TRIGGER_KEY = "invitationExpirationTrigger";
    
    private static final String INVITATION_REMINDER_JOB_KEY = "invitationReminderJob";
    private static final String INVITATION_REMINDER_TRIGGER_KEY = "invitationReminderTrigger";
    
    private static final String FORGOT_PASSWORD_JOB_KEY = "forgotPasswordExpirationJob";
    private static final String FORGOT_PASSWORD_TRIGGER_KEY = "forgotPasswordExpirationTrigger";
    
    private static final String USER_VERIFICATION_JOB_KEY = "userVerificationReminderJob";
    private static final String USER_VERIFICATION_TRIGGER_KEY = "userVerificationReminderTrigger";
    
    private static final String JOB_GROUP = "common";
    
    @Inject
    private Environment environment;
    
    @Inject
    private AdDao adDao;
    @Inject
    private UserDao userDao;
    @Inject
    private InvitationDao invitationDao;
    @Inject
    private ForgotPasswordDao forgotPasswordDao;
    @Inject
    private UserVerificationDao userVerificationDao;
    @Inject
    private EmailSender emailSender;
    
    private int adExpirationIntervalCheckSecond;
    private int adOnlineStartingCheckHour;
    private int invitationExpirationIntervalCheckSecond;
    private int invitationExpirationReminderCheckSecond;
    private int forgotPasswordExpirationIntervalCheckSecond;
    private int userVerificationReminderIntervalCheckSecond;
    
    @PostConstruct
    public void init() {
        adExpirationIntervalCheckSecond = environment.getProperty("job.ad.expirationIntervalCheckSecond", int.class);
        adOnlineStartingCheckHour = environment.getProperty("job.ad.onlineStartingCheckHour", int.class);
        invitationExpirationIntervalCheckSecond = environment.getProperty("job.invitation.expirationIntervalCheckSecond", int.class);
        invitationExpirationReminderCheckSecond = environment.getProperty("job.invitation.expirationReminderCheckSecond", int.class);
        forgotPasswordExpirationIntervalCheckSecond = environment.getProperty("job.forgotPassword.expirationIntervalCheckSecond", int.class);
        userVerificationReminderIntervalCheckSecond = environment.getProperty("job.user.verificationReminderIntervalCheckSecond", int.class);
    }
    
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.scheduleJob(adExpirationJobDetail(), adExpirationTrigger());
        scheduler.scheduleJob(adOnlineJobDetail(), adOnlineTrigger());
        scheduler.scheduleJob(invitationExpirationJobDetail(), invitationExpirationTrigger());
        scheduler.scheduleJob(invitationReminderJobDetail(), invitationReminderTrigger());
        scheduler.scheduleJob(forgotPasswordExpirationJobDetail(), forgotPasswordExpirationTrigger());
        return scheduler;
    }
    
    // Ad related job configs
    
    @Bean
    public JobDetail adExpirationJobDetail() {
        JobDetail job = createJobDetail(AdExpirationJob.class, AD_EXPIRATION_JOB_KEY);
        job.getJobDataMap().put(Constants.AD_DAO, adDao);
        return job;
    }

    @Bean
    public Trigger adExpirationTrigger() {
        return createRepeatTrigger(AD_EXPIRATION_TRIGGER_KEY, adExpirationIntervalCheckSecond);
    }
    
    @Bean
    public JobDetail adOnlineJobDetail() {
        JobDetail job = createJobDetail(AdOnlineJob.class, AD_ONLINE_JOB_KEY);
        job.getJobDataMap().put(Constants.AD_DAO, adDao);
        job.getJobDataMap().put(Constants.USER_DAO, userDao);
        job.getJobDataMap().put(Constants.EMAIL_SENDER, emailSender);
        return job;
    }

    @Bean
    public Trigger adOnlineTrigger() {
        return createDailyTrigger(AD_ONLINE_TRIGGER_KEY, adOnlineStartingCheckHour);
    }
    
    // Invitation related job config
    
    @Bean
    public JobDetail invitationExpirationJobDetail() {
        JobDetail job = createJobDetail(InvitationExpirationJob.class, INVITATION_JOB_KEY);
        job.getJobDataMap().put(Constants.INVITATION_DAO, invitationDao);
        return job;
    }
    
    @Bean
    public Trigger invitationExpirationTrigger() {
        return createRepeatTrigger(INVITATION_TRIGGER_KEY, invitationExpirationIntervalCheckSecond);
    }
    
    @Bean
    public JobDetail invitationReminderJobDetail() {
        JobDetail job = createJobDetail(InvitationReminderJob.class, INVITATION_REMINDER_JOB_KEY);
        job.getJobDataMap().put(Constants.INVITATION_DAO, invitationDao);
        job.getJobDataMap().put(Constants.EMAIL_SENDER, emailSender);
        return job;
    }
    
    @Bean
    public Trigger invitationReminderTrigger() {
        return createRepeatTrigger(INVITATION_REMINDER_TRIGGER_KEY, invitationExpirationReminderCheckSecond);
    }
    
    // ForgotPassword requests related job config
    
    @Bean
    public JobDetail forgotPasswordExpirationJobDetail() {
        JobDetail job = createJobDetail(ForgotPasswordExpirationJob.class, FORGOT_PASSWORD_JOB_KEY);
        job.getJobDataMap().put(Constants.FORGOT_PASSWORD_DAO, forgotPasswordDao);
        return job;
    }
    
    @Bean
    public Trigger forgotPasswordExpirationTrigger() {
        return createRepeatTrigger(FORGOT_PASSWORD_TRIGGER_KEY, forgotPasswordExpirationIntervalCheckSecond);
    }
    
    // UserVerification reminder related job config
    
    @Bean
    public JobDetail userVerificationReminderJobDetail() {
        JobDetail job = createJobDetail(UserVerificationReminderJob.class, USER_VERIFICATION_JOB_KEY);
        job.getJobDataMap().put(Constants.USER_VERIFICATION_DAO, userVerificationDao);
        job.getJobDataMap().put(Constants.EMAIL_SENDER, emailSender);
        return job;
    }
    
    @Bean
    public Trigger userVerificationReminderTrigger() {
        return createRepeatTrigger(USER_VERIFICATION_TRIGGER_KEY, userVerificationReminderIntervalCheckSecond);
    }
    
    // internal
    
    private JobDetail createJobDetail(Class <? extends Job> jobClass, String identity) {
        return newJob(jobClass)
                .withDescription("JobDetail (class: " + jobClass.getSimpleName() + ", identity: " + identity + ")")
                .withIdentity(identity, JOB_GROUP)
                .build();
    }
    
    private Trigger createRepeatTrigger(String identity, int intervalInSeconds) {
        return newTrigger()
                .withDescription("Trigger (identity: " + identity + ")")
                .withIdentity(identity, JOB_GROUP)
                .withSchedule(repeatSecondlyForever(intervalInSeconds))
                .build();
    }
    
    /**
     * See:
     * http://quartz-scheduler.org/documentation/quartz-2.1.x/cookbook/DailyTrigger
     * 
     * @param identity
     * @param startingHour
     * @return 
     */
    private Trigger createDailyTrigger(String identity, int startingHour) {
        return newTrigger()
                .withDescription("Trigger (identity: " + identity + ")")
                .withIdentity(identity, JOB_GROUP)
                .startNow()
                .withSchedule(dailyAtHourAndMinute(startingHour, 0))
                .build();
    }
}