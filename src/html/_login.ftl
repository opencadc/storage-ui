<form class="form-inline" id="loginForm" role="form" method="post"
      action="/beacon/ac/login">
  <div class="form-group">
    <label for="username" class="wb-inv" id="usernameLabel">Username</label>
    <input type="text" id="username" name="username" class="form-control" tabindex="1" required="required" placeholder="Username" />
  </div>
  <div class="form-group">
    <label for="password" class="wb-inv" id="passwordLabel">Password</label>
    <input type="password" id="password" name="password" class="form-control" tabindex="2" required="required" placeholder="Password" />
  </div>
  <input type="hidden" id="redirectPath" name="redirectPath" value="${folder.path}" />
  <button type="submit" class="btn btn-default">Login</button>
</form>
<#if username??>
<p>Currently logged in as <strong>${username}</strong></p>
</#if>
