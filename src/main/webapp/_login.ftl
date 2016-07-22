<div id="navbar" class="navbar-collapse collapse">
  <#if username??>
    <ul class="navbar-nav navbar-right list-unstyled">
      <li>
        <a class="btn btn-md" id="logout" role="button" title="Logout.">
          <span class="glyphicon glyphicon-log-out"></span></a></li>
      <li class="dropdown">
        <a title="User actions." class="dropdown-toggle btn btn-md" role="button" aria-expanded="false" data-toggle="dropdown">
          ${username}<span class="caret"></span></a>&nbsp;&nbsp;
        <ul class="dropdown-menu">
          <li>
            <a title="Update profile." href="#">My profile</a></li>
          <li>
            <a title="Change password." href="#">Change password</a></li>
        </ul></li>
    </ul>
  <#else>
    <form class="navbar-form navbar-right" id="loginForm" role="form" method="post"
          action="/storage/ac/authenticate">
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
  </#if>
</div>
