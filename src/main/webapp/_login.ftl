<div id="navbar" class="navbar-collapse collapse">
  <form class="navbar-form navbar-right" id="loginForm" role="form" method="post"
        action="/beacon/ac/login">
    <#if username??><em class="auth-info">Logged in as ${username}</em>&nbsp;&nbsp;</#if>
    <div class="form-group">
      <label for="username" class="hidden" id="usernameLabel">Username</label>
      <input type="text" id="username" name="username" class="form-control" tabindex="1" required="required" placeholder="Username" />
    </div>
    <div class="form-group">
      <label for="password" class="hidden" id="passwordLabel">Password</label>
      <input type="password" id="password" name="password" class="form-control" tabindex="2" required="required" placeholder="Password" />
    </div>
    <input type="hidden" id="redirectPath" name="redirectPath" value="${folder.path}" />
    <button type="submit" class="btn btn-success">Login</button>
  </form>
</div>
