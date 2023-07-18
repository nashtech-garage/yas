<#import "template.ftl" as layout>
<#import "components/link/primary.ftl" as linkPrimary>

<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "title">
         ${msg("registerTitle",(realm.displayName!''))}
    <#elseif section = "header">
        <link href="https://fonts.googleapis.com/css?family=Muli" rel="stylesheet"/>
        <script>
            function togglePassword() {
                var x = document.getElementById("password");
                var v = document.getElementById("vi");
                if (x.type === "password") {
                    x.type = "text";
                    v.src = "${url.resourcesPath}/img/eye.png";
                } else {
                    x.type = "password";
                    v.src = "${url.resourcesPath}/img/eye-off.png";
                }
            }
        </script>
            <div class="logoyas">
           <img class="logo" src="${url.resourcesPath}/img/yaslogo.png" alt="yas">
        </div>
    <#elseif section = "form">
      <div class="box-container">
            <div>
                <p class="application-name">Welcome to Yas store</p>
            </div>
     
        <form id="kc-register-form" class="form" onsubmit="return true;" action="${url.registrationAction}" method="post">
           
            <div class="input-group">                
                    <input type="text" id="firstName" class="login-field" name="firstName" 
                          placeholder="${msg("firstName")}"
                           value="${(register.formData.firstName!'')}"
                           aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"
                    />
                    <#if messagesPerField.existsError('firstName')>
                        <span id="input-error-firstname" class="error-message" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('firstName'))?no_esc}
                        </span>
                    </#if>
            </div>

            <div class="input-group">       
                    <input type="text" id="lastName" class="login-field" name="lastName" 
                          placeholder="${msg("lastName")}" 
                           value="${(register.formData.lastName!'')}"
                           aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"
                    />
                    <#if messagesPerField.existsError('lastName')>
                        <span id="input-error-lastname" class="error-message" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('lastName'))?no_esc}
                        </span>
                    </#if>                
            </div>

            <div class="input-group">               
                    <input type="text" id="email" class="login-field" name="email"
                          placeholder="${msg("email")}"
                           value="${(register.formData.email!'')}" autocomplete="email"
                           aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"
                    />
                    <#if messagesPerField.existsError('email')>
                        <span id="input-error-email" class="error-message" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('email'))?no_esc}
                        </span>
                    </#if>        
            </div>

            <#if !realm.registrationEmailAsUsername>
                <div class="input-group">                   
                        <input type="text" id="username" class="login-field" name="username"
                              placeholder="${msg("username")}"
                               value="${(register.formData.username!'')}" autocomplete="username"
                               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"
                        />
                        <#if messagesPerField.existsError('username')>
                            <span id="input-error-username" class="error-message" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('username'))?no_esc}
                            </span>
                        </#if>
                </div>
            </#if>

            <#if passwordRequired??>
                    <div class="input-group">
                        <input type="password" id="password" class="login-field" name="password"
                              placeholder="${msg("password")}"
                               autocomplete="new-password"
                               aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                        />
                        <#if messagesPerField.existsError('password')>
                            <span id="input-error-password" class="error-message" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('password'))?no_esc}
                            </span>
                        </#if>
                </div>

                <div class="input-group">
                        <input type="password" id="password-confirm" class="login-field"
                              placeholder="${msg("passwordConfirm")}"
                               name="password-confirm"
                               aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                        />
                        <#if messagesPerField.existsError('password-confirm')>
                            <span id="input-error-password-confirm" class="error-message" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                            </span>
                        </#if>
                </div>
            </#if>

            <#if recaptchaRequired??>
                <div class="form-group">
                        <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>                    
                </div>
            </#if>

            <div class="wrapper-button">
                <div id="kc-form-options" class="backtologin">
                  <a class="" href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a>
                </div>
                 <input class="register-submit" type="submit" value="${msg("doRegister")}"/>
            </div>
        </form>

        <div>
            <p class="copyright">&copy; copyright - yas.nashtech-garage ${.now?string('yyyy')}</p>
        </div>
    </#if>
</@layout.registrationLayout>