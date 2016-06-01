<section class="panel panel-<#if username??>success<#else>default</#if>">
  <div class="panel-body">
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
  </div>
  <#if username??>
    <footer class="panel-heading">
      <h4 class="panel-title">Currently logged in as ${username}</h4>
    </footer>
  </#if>
</section>
