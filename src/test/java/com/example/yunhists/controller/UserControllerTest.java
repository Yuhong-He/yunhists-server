package com.example.yunhists.controller;

import com.example.yunhists.YunhistsServerApplication;
import com.example.yunhists.service.EmailTimerService;
import com.example.yunhists.service.EmailVerificationService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.JwtHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = YunhistsServerApplication.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    EmailVerificationService evService;

    @Autowired
    EmailTimerService etService;

    private final String testUsername = "test";
    private final String testEmail = "test@yunnanhistory.com";
    private final String testEmail2 = "test2@yunnanhistory.com";
    private final String expiredEmail = "test1@yunnanhistory.com";
    private final String testPassword = "testtest";
    private final String newTestPassword = "testtesttest";
    private final String shortPassword = "test";
    private final String testCode = "114514";
    static int testUserId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Order(1)
    @Test
    public void sendRegisterEmail_validEmail_200success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendRegisterEmail")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(2)
    @Test
    public void sendRegisterEmail_sendAgain_211WaitOneMinute() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendRegisterEmail")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":211"));
    }

    @Order(3)
    @Test
    public void sendRegisterEmail_DMInternalError_212EmailFail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList("test@yunnanhistory.c"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendRegisterEmail")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":212"));
    }

    @Order(4)
    @Test
    public void sendRegisterEmail_invalidEmail_210InvalidEmail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList("test"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendRegisterEmail")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":210"));
    }

    @Order(11)
    @Test
    public void register_usernameInvalid_216UsernameLength() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList("t"));
        params.put("password", Collections.singletonList(shortPassword));
        params.put("password2", Collections.singletonList(shortPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":216"));
    }

    @Order(12)
    @Test
    public void register_passwordInvalid_217PasswordLength() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(shortPassword));
        params.put("password2", Collections.singletonList(shortPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":217"));
    }

    @Order(13)
    @Test
    public void register_passwordNotMatch_207PasswordNotMatch() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList("testtes"));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":207"));
    }

    @Order(13)
    @Test
    public void register_emailRegistered_215EmailAlreadyRegistered() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList("test0@yunnanhistory.com"));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":215"));
    }

    @Order(15)
    @Test
    public void register_emailVerificationNotSend_218NoVerificationCode() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList("admin@yunnanhistory.com"));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":218"));
    }

    @Order(16)
    @Test
    public void register_emailVerificationExpired_213VerificationCodeExpired() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(expiredEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":213"));
    }

    @Order(17)
    @Test
    public void register_incorrectVerificationCode_214VerificationCodeIncorrect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":214"));
    }

    @Order(18)
    @Test
    public void register_invalidLang_204LangNotSupport() throws Exception {
        String code = evService.read(testEmail).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("fr"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(code));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":204"));
    }

    @Order(19)
    @Test
    public void register_correctVerificationCode_200success() throws Exception {
        String code = evService.read(testEmail).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList("zh"));
        params.put("email", Collections.singletonList(testEmail));
        params.put("username", Collections.singletonList(testUsername));
        params.put("password", Collections.singletonList(testPassword));
        params.put("password2", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(code));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(21)
    @Test
    public void login_withoutParams_400() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertEquals("", responseString);
    }

    @Order(22)
    @Test
    public void login_validUser_200Success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail));
        params.put("password", Collections.singletonList(testPassword));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        assertTrue(responseString.startsWith("{\"code\":200"));
        testUserId = userService.getUserByEmail(testEmail).getId();
    }

    @Order(23)
    @Test
    public void login_wrongPwd_206IncorrectPwd() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail));
        params.put("password", Collections.singletonList("testtes["));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":206"));
    }

    @Order(24)
    @Test
    public void login_googleAccount_209GoogleAccount() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test@gmail.com"));
        params.put("password", Collections.singletonList(""));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":209"));
    }

    @Order(25)
    @Test
    public void login_emailNotRegistered_208EmailNotRegistered() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("unregistered@yunnanhistory.com"));
        params.put("password", Collections.singletonList(testPassword));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":208"));
    }

    @Order(31)
    @Test
    public void updateLang_validUser_200success() throws Exception {
        String lang = "en";
        String token = JwtHelper.createToken((long) testUserId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList(lang));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateLang")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
        assertEquals(lang, userService.getUserById(testUserId).getLang());
    }

    @Order(32)
    @Test
    public void updateLang_invalidUserId_205NoUser() throws Exception {
        String lang = "en";
        int userId = 114514;
        String token = JwtHelper.createToken((long) userId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList(lang));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateLang")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(33)
    @Test
    public void updateLang_invalidLang_204LangNotSupport() throws Exception {
        String lang = "fr";
        String token = JwtHelper.createToken((long) testUserId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("lang", Collections.singletonList(lang));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateLang")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":204"));
    }

    @Order(41)
    @Test
    public void updateUsername_validUser_200success() throws Exception {
        String username = "newUsername";
        String token = JwtHelper.createToken((long) testUserId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("username", Collections.singletonList(username));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateUsername")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
        assertEquals(username, userService.getUserById(testUserId).getUsername());
    }

    @Order(42)
    @Test
    public void updateUsername_invalidUsername_216UsernameLength() throws Exception {
        String username = "n";
        String token = JwtHelper.createToken((long) testUserId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("username", Collections.singletonList(username));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateUsername")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":216"));
    }

    @Order(43)
    @Test
    public void updateUsername_InvalidUserId_205NoUser() throws Exception {
        String username = "newUsername";
        int userId = 114514;
        String token = JwtHelper.createToken((long) userId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("username", Collections.singletonList(username));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateUsername")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(51)
    @Test
    public void sendChangeEmailEmail_validEmail_200success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendChangeEmailEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(52)
    @Test
    public void sendChangeEmailEmail_sendAgain_211WaitOneMinute() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendChangeEmailEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":211"));
    }

    @Order(53)
    @Test
    public void sendChangeEmailEmail_DMInternalError_212EmailFail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test@yunnanhistory.c"));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendChangeEmailEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":212"));
    }

    @Order(54)
    @Test
    public void sendChangeEmailEmail_invalidEmail_210InvalidEmail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test"));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendChangeEmailEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":210"));
    }

    @Order(55)
    @Test
    public void sendChangeEmailEmail_InvalidUserId_205NoUser() throws Exception {
        int userId = 114514;
        String token = JwtHelper.createToken((long) userId);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sendChangeEmailEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(61)
    @Test
    public void updateEmail_validCode_200success() throws Exception {
        String code = evService.read(testEmail2).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        params.put("password", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(code));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(62)
    @Test
    public void updateEmail_inCorrectCode_214VerificationCodeIncorrect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        params.put("password", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":214"));
    }

    @Order(63)
    @Test
    public void updateEmail_emailVerificationExpired_213VerificationCodeExpired() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(expiredEmail));
        params.put("password", Collections.singletonList("123456"));
        params.put("code", Collections.singletonList(testCode));
        String token = JwtHelper.createToken((long) 2);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":213"));
    }

    @Order(64)
    @Test
    public void updateEmail_emailVerificationNotSend_218NoVerificationCode() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("admin@yunnanhistory.com"));
        params.put("password", Collections.singletonList(testPassword));
        params.put("code", Collections.singletonList(testCode));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":218"));
    }

    @Order(65)
    @Test
    public void updateEmail_incorrectPassword_206PasswordIncorrect() throws Exception {
        String code = evService.read(testEmail2).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        params.put("password", Collections.singletonList("123456"));
        params.put("code", Collections.singletonList(code));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":206"));
    }

    @Order(66)
    @Test
    public void updateEmail_googleRegisteredAccount_209RegisterWithGoogle() throws Exception {
        String code = evService.read(testEmail2).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        params.put("password", Collections.singletonList("123456"));
        params.put("code", Collections.singletonList(code));
        String token = JwtHelper.createToken((long) 3);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":209"));
    }

    @Order(67)
    @Test
    public void updateEmail_invalidUserId_205UserNoteExist() throws Exception {
        String code = evService.read(testEmail2).getVerificationCode();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        params.put("password", Collections.singletonList("123456"));
        params.put("code", Collections.singletonList(code));
        String token = JwtHelper.createToken((long) 114514);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updateEmail")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(71)
    @Test
    public void updatePassword_invalidUserId_205UserNoteExist() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("oldPwd", Collections.singletonList(testPassword));
        params.put("newPwd", Collections.singletonList(newTestPassword));
        params.put("newPwd2", Collections.singletonList(newTestPassword));
        String token = JwtHelper.createToken((long) 114514);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updatePassword")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(72)
    @Test
    public void updatePassword_passwordIncorrect_206IncorrectPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("oldPwd", Collections.singletonList("123456"));
        params.put("newPwd", Collections.singletonList(newTestPassword));
        params.put("newPwd2", Collections.singletonList(newTestPassword));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updatePassword")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":206"));
    }

    @Order(73)
    @Test
    public void updatePassword_shortPassword_217PasswordLengthIncorrect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("oldPwd", Collections.singletonList(testPassword));
        params.put("newPwd", Collections.singletonList("test"));
        params.put("newPwd2", Collections.singletonList(newTestPassword));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updatePassword")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":217"));
    }

    @Order(74)
    @Test
    public void updatePassword_newPasswordNotMatch_207PasswordNotMatch() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("oldPwd", Collections.singletonList(testPassword));
        params.put("newPwd", Collections.singletonList(newTestPassword + "123"));
        params.put("newPwd2", Collections.singletonList(newTestPassword));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updatePassword")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":207"));
    }

    @Order(75)
    @Test
    public void updatePassword_validPassword_200success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("oldPwd", Collections.singletonList(testPassword));
        params.put("newPwd", Collections.singletonList(newTestPassword));
        params.put("newPwd2", Collections.singletonList(newTestPassword));
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/updatePassword")
                        .header("token", token)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
        assertNotNull(userService.login(testEmail2, newTestPassword));
    }

    @Order(81)
    @Test
    public void resetPassword_validEmail_200success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/resetPassword")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(82)
    @Test
    public void resetPassword_sendAgain_211WaitOneMinute() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList(testEmail2));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/resetPassword")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":211"));
    }

    @Order(83)
    @Test
    public void resetPassword_inValidEmail_210InvalidEmailAddress() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/resetPassword")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":210"));
    }

    @Order(84)
    @Test
    public void resetPassword_emailNotRegistered_208EmailNotRegistered() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("example@yunnanhistory.com"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/resetPassword")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":208"));
    }

    @Order(85)
    @Test
    public void resetPassword_DMInternalError_212EmailFail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test@yunnanhistory.c"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/resetPassword")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":212"));
    }

    @Order(91)
    @Test
    public void delete_invalidUserId_205NoUser() throws Exception {
        int userId = 114514;
        String token = JwtHelper.createToken((long) userId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/delete")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":205"));
    }

    @Order(92)
    @Test
    public void delete_validUser_200Success() throws Exception {
        String token = JwtHelper.createToken((long) testUserId);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/delete")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @AfterAll
    public void cleanUp() {
        userService.deleteUserById(testUserId);
        evService.delete(evService.read(testEmail).getId());
        evService.delete(evService.read(testEmail2).getId());
        etService.delete(etService.read(testEmail, "verificationCode").getId());
        etService.delete(etService.read(testEmail2, "verificationCode").getId());
        etService.delete(etService.read(testEmail2, "resetPwd").getId());
    }

}
