<div id="navbar" class="navbar-collapse collapse">
  <form class="navbar-form navbar-right" id="loginForm" role="form" method="post"
        action="/beacon/ac/login">
    <#if username??>
      <a href="#" title="Logout.">
        <span class="glyphicon glyphicon-log-out"></span></a>&nbsp;&nbsp;
      <a href="#" title="User actions." class="dropdown-toggle" aria-expanded="false" data-toggle="dropdown">
        <em class="auth-info">${username}</em><span class="caret"></span></a>&nbsp;&nbsp;
    </#if>
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
