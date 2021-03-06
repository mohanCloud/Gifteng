package com.venefica.service.dto;

import com.venefica.dao.AddressWrapperDao;
import com.venefica.dao.ImageDao;
import com.venefica.model.AddressWrapper;
import com.venefica.model.BusinessUserData;
import com.venefica.model.Gender;
import com.venefica.model.Image;
import com.venefica.model.ImageModelType;
import com.venefica.model.MemberUserData;
import com.venefica.model.User;
import com.venefica.model.UserPoint;
import com.venefica.model.UserSocialPoint;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * User data transfer object.
 *
 * @author Sviatoslav Grebenchukov
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDto extends DtoBase {
    
    // out
    private Long id;
    // in, out
    private String name;
    // in, out
    private String firstName;
    // in, out
    private String lastName;
    // in, out
    private String email;
    // in, out
    private String phoneNumber;
    // in, out
    private Date dateOfBirth;
    // in, out
    private String about;
    // in, out
    private ImageDto avatar;
    // out
    private Date joinedAt;
    // out
    private Date lastLoginAt;
    // out
    private Date previousLoginAt;
    // out
    private boolean inFollowers;
    // out
    private boolean inFollowings;
    // in, out
    private Gender gender;
    // in, out
    private AddressDto address;
    // in, out
    private boolean businessAccount;
    // out
    private BigDecimal score;
    // out
    private BigDecimal pendingScore;
    // out
    private BigDecimal businessScore;
    // out
    private UserStatisticsDto statistics;
    // out
    private boolean verified;
    
    // business user data
    
    // in, out
    private String businessName;
    // in, out
    private String contactName;
    // in, out
    private String website;
    // in, out
    private String contactNumber;
    // in, out
    private Long businessCategoryId;
    // in, out
    private String businessCategory;
    // in, out
    private Set<AddressDto> addresses;
    
    // Required for JAX-WS
    public UserDto() {
    }
    
    /**
     * Constructs the DTO object form the domain object.
     *
     * @param user domain object
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public UserDto(User user) {
        this(user, true);
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public UserDto(User user, boolean includeUserPoints) {
        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        about = user.getAbout();
        website = user.getWebsite();
        joinedAt = user.getJoinedAt();
        lastLoginAt = user.getLastLoginAt();
        previousLoginAt = user.getPreviousLoginAt();
        avatar = user.getAvatar() != null ? new ImageDto(user.getAvatar()) : null;
        address = new AddressDto(user.getAddress(), user.getLocation());
        businessAccount = user.isBusinessAccount();
        verified = user.isVerified();
        user.getUserData().updateUserDto(this);
        
        if ( includeUserPoints && user.getUserPoint() != null ) {
            if ( !user.isBusinessAccount() ) {
                UserPoint userPoint = user.getUserPoint();
                MemberUserData memberUserData = (MemberUserData) user.getUserData();
                UserSocialPoint socialPoint = memberUserData.getUserSocialPoint();
                
                score = userPoint.getGivingNumber();
                pendingScore = userPoint.getCalculatedPendingGivingNumber();
                businessScore = score
                        .subtract(userPoint.getBusinessReceivingNumber())
                        .add(socialPoint != null ? new BigDecimal(socialPoint.getSocialPoint()) : BigDecimal.ZERO);
            }
        }
    }

    /**
     * Updates the domain object using values from the DTO object.
     *
     * @param user domain object to update
     */
    public void update(User user, ImageDao imageDao, AddressWrapperDao addressWrapperDao) {
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAbout(about);
        user.setWebsite(website);
        user.setAddress(address != null ? address.toAddress() : null);
        user.setLocation(address != null ? address.toLocation() : null);
        user.getUserData().updateUser(this);
        
        try {
            // Handle avatar image
            if (avatar != null && avatar.getImgType() != null && avatar.getData() != null) {
                if (user.getAvatar() != null) {
                    Image avatarImage = user.getAvatar();
                    user.setAvatar(null);
                    imageDao.delete(avatarImage, ImageModelType.USER);
                }

                Image avatarImage = avatar.toImage();
                imageDao.save(avatarImage, ImageModelType.USER);

                // Set new avatar image
                user.setAvatar(avatarImage);
            }
        } catch ( IOException ex ) {
            //TODO: needs logging?
        }
        
        if ( user.isBusinessAccount() ) {
            Set<AddressWrapper> addressWrappers = ((BusinessUserData) user.getUserData()).getAddresses();
            if ( addressWrappers != null && !addressWrappers.isEmpty() ) {
                for ( AddressWrapper addressWrapper : addressWrappers ) {
                    addressWrapperDao.saveOrUpdate(addressWrapper);
                }
            }
        }
    }

    public User toMemberUser(ImageDao imageDao, AddressWrapperDao addressWrapperDao) {
        User user = new User();
        user.setUserData(new MemberUserData());
        update(user, imageDao, addressWrapperDao);
        return user;
    }
    
    public User toBusinessUser(ImageDao imageDao, AddressWrapperDao addressWrapperDao) {
        User user = new User();
        user.setUserData(new BusinessUserData());
        update(user, imageDao, addressWrapperDao);
        return user;
    }
    
    public void addAddress(AddressDto addressDto) {
        if ( addresses == null ) {
            addresses = new LinkedHashSet<AddressDto>();
        }
        addresses.add(addressDto);
    }
    
    // getter/setter
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImageDto getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageDto avatar) {
        this.avatar = avatar;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    //public void setJoinedAt(Date joinedAt) {
    //    this.joinedAt = joinedAt;
    //}

    public boolean isInFollowers() {
        return inFollowers;
    }

    public void setInFollowers(boolean inFollowers) {
        this.inFollowers = inFollowers;
    }

    public boolean isInFollowings() {
        return inFollowings;
    }

    public void setInFollowings(boolean inFollowings) {
        this.inFollowings = inFollowings;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public boolean isBusinessAccount() {
        return businessAccount;
    }

    public void setBusinessAccount(boolean businessAccount) {
        this.businessAccount = businessAccount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getBusinessCategoryId() {
        return businessCategoryId;
    }

    public void setBusinessCategoryId(Long businessCategoryId) {
        this.businessCategoryId = businessCategoryId;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public Set<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public UserStatisticsDto getStatistics() {
        return statistics;
    }

    public void setStatistics(UserStatisticsDto statistics) {
        this.statistics = statistics;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getPendingScore() {
        return pendingScore;
    }

    public void setPendingScore(BigDecimal pendingScore) {
        this.pendingScore = pendingScore;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public BigDecimal getBusinessScore() {
        return businessScore;
    }

    public void setBusinessScore(BigDecimal businessScore) {
        this.businessScore = businessScore;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Date getPreviousLoginAt() {
        return previousLoginAt;
    }

    public void setPreviousLoginAt(Date previousLoginAt) {
        this.previousLoginAt = previousLoginAt;
    }
}
