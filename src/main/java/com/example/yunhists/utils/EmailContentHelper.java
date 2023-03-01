package com.example.yunhists.utils;

public class EmailContentHelper {

    public static String getRegisterVerificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "注册验证码";
        } else {
            return "Register Verification Code";
        }
    }

    public static String getResetPasswordEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "重置密码";
        } else {
            return "Reset Password";
        }
    }

    public static String getChangeEmailVerificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "邮箱验证码";
        } else {
            return "Email Verification Code";
        }
    }

    public static String getDeleteThesisNotificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "删除通知";
        } else {
            return "Delete Notification";
        }
    }

    public static String getNewShareNotificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "新分享通知";
        } else {
            return "New Sharing Notification";
        }
    }

    public static String getShareApprovedNotificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "批准通知";
        } else {
            return "Approved Notification";
        }
    }

    public static String getShareRejectedNotificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "驳回通知";
        } else {
            return "Reject Notification";
        }
    }

    public static String getRegisterVerificationEmailBody(String lang, String code) {
        if(lang.equals("zh")) {
            return "<p>新用户您好，这是您的注册验证码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>验证码五分钟内有效，请尽快验证。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Welcome, this is your verification code: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>The verification code has 5 minutes expiration. Please verify as soon as possible.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getResetPasswordEmailBody(String lang, String username, String password) {
        if(lang.equals("zh")) {
            return "<p>" + username + "您好，这是您的新密码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + password + "</p>" +
                    "<p>请使用新密码登录，然后在<a href=\"https://www.yunnanhistory.com/profile\">您的用户页</a>修改密码，请不要将密码透露给他人。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Hello " + username + ", this is your new password: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + password + "</p>" +
                    "<p>Please login with the new password, then change password at <a href=\"https://www.yunnanhistory.com/profile\">your profile page</a>. Please do not disclose your password to others.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getChangeEmailVerificationEmailBody(String lang, String username, String code) {
        if(lang.equals("zh")) {
            return "<p>" + username + "您好，这是您的改绑邮箱验证码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>验证码五分钟内有效，请尽快验证。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Hello " + username + ", this is your change email verification code: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>The verification code has 5 minutes expiration. Please verify as soon as possible.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getDeleteThesisNotificationEmailBody(String lang, String username, String title, String reason, String admin) {
        if(reason.isEmpty()) {
            reason = "<--- NULL --->";
        }
        if(lang.equals("zh")) {
            if(!reason.equals("<--- NULL --->")) {
                reason = DeepL.translateToZh(reason);
            }
            return "<p>" + username + "您好，</p>" +
                    "<p style='text-indent: 2em'>很抱歉的通知您，您分享的论文《" + title + "》已被删除。理由是：</p>" +
                    "<p style='padding-left: 4em; padding-right: 4em; color: #666666'>" + reason + "</p>" +
                    "<p style='text-indent: 2em'>执行此操作的管理员是：" + admin + "，如您对此有疑问，请与我们联系。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            if(!reason.equals("<--- NULL --->")) {
                reason = DeepL.translateToEn(reason);
            }
            return "<p>Hello " + username + ",</p>" +
                    "<p style='text-indent: 2em'>We are sorry to inform you that the paper \"<span style='font-style:oblique'>" + title + "\" you shared has been deleted. The reason is:</p>" +
                    "<p style='padding-left: 4em; padding-right: 4em; color: #666666'>" + reason + "</p>" +
                    "<p style='text-indent: 2em'>The administrator who performed this operation is:" + admin + ", please feel free to contact us if you have any questions about this.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getNewShareNotificationEmailBody(String lang) {
        if(lang.equals("zh")) {
            return "<p>管理员您好，滇史论辑收到新的分享，请审批。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Dear admin, Yunhists received new sharing. Please approve.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getShareApprovedNotificationEmailBody(String lang, String username, String title) {
        if(lang.equals("zh")) {
            return "<p>" + username + "您好，</p>" +
                    "<p>您分享的论文《" + title + "》已审批通过，非常感谢您的贡献！</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Dear " + username + ",</p>" +
                    "<p>The thesis you shared <span style='font-style:oblique'>" + title + "</span> has been approved. Thank you very much for your contribution.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getShareRejectedNotificationEmailBody(String lang, String username, String title, String reason) {
        if(lang.equals("zh")) {
            reason = DeepL.translateToZh(reason);
            return "<p>" + username + "您好，</p>" +
                    "<p>很抱歉，您分享的论文《" + title + "》未通过审批，理由是：" + reason + "</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            reason = DeepL.translateToEn(reason);
            return "<p>Dear " + username + ",</p>" +
                    "<p>We are sorry to inform you, the thesis you shared <span style='font-style:oblique'>" + title + "</span> has been rejected. The reason is: " + reason + "</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

}
