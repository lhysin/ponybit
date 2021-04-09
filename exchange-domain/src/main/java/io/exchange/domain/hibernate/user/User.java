package io.exchange.domain.hibernate.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.StringUtils;

import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.Role;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.Wallet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@TableGenerator(name = "user_table_generator", 
                table = "sequence",
                pkColumnName = "sequence_name",
                pkColumnValue = "user_sequence",
                valueColumnName = "next_val",
                allocationSize = 1,
                initialValue = 0)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_table_generator")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable=false)
    private String email;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @Column(nullable=false)
    private String myRefCd;

    private String otherRefCd;

    private LocalDateTime delDtm;

    private String otpHash;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
    private Collection<Wallet> wallets;

    public void changePwd(String pwd) {
        this.pwd = pwd;
    }

    public void changeUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public void setOtherRefCd(String otherRefCd) {
        if(!StringUtils.isEmpty(this.otherRefCd)) {
            throw new BusinessException(Code.REF_ALREADY_EXISTS);
        } else {
            this.otherRefCd = otherRefCd;
        }
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements Email, Pwd, UserLevelType, RoleEnum, MyRefCd, RegIp, RegDtm {
        UserBuilder builder = User.privateBuilder();

        @Override
        public Pwd email(String email) {
            builder.email(email);
            return this;
        }

        @Override
        public UserLevelType pwd(String pwd) {
            builder.pwd(pwd);
            return this;
        }

        @Override
        public RoleEnum userLevel(UserLevel userLevel) {
            builder.userLevel(userLevel);
            return this;
        }

        @Override
        public MyRefCd role(Role role) {
            builder.role(role);
            return this;
        }

        @Override
        public RegIp myRefCd(String myRefCd) {
            builder.myRefCd(myRefCd);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public UserBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static Email builder() {
        return new SafeBuilder();
    }

    public interface Email {
        Pwd email(String email);
    }
    public interface Pwd {
        UserLevelType pwd(String pwd);
    }
    public interface UserLevelType {
        RoleEnum userLevel(UserLevel userLevel);
    }
    public interface RoleEnum {
        MyRefCd role(Role role);
    }
    public interface MyRefCd {
        RegIp myRefCd(String myRefCd);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        UserBuilder regDtm(LocalDateTime regDtm);
    }
}