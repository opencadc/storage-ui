<div class="row">
  <div role="navigation" class="col-sm-3 col-md-2 sidebar">
    <ul class="nav nav-sidebar">
      <li><a href="http://www.canfar.phys.uvic.ca/canfar/groups">&nbsp;Manage Groups</a></li>
      <li><a href="#" title="Space allocations.">&nbsp;Manage Allocations</a></li>
    </ul>

    <ul class="nav nav-sidebar">
      <li><a href="http://www.canfar.net/docs/vospace/">&nbsp;Web Service Documentation</a></li>
    </ul>

    <ul class="nav nav-sidebar">
      <li><a href="http://www.github.com/opencadc/vosui">&nbsp;GitHub</a></li>
    </ul>
    <form id="uploader" method="post">
      <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
    </form>
  </div>

  <!-- Main content -->
  <div role="main" class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
    <h1 class="hidden" property="name">${folder.path}</h1>
    <section>
      <h2 property="name">
      <#if isRoot>
        ROOT
      <#else>
        <button id="more_details" name="more_details" class="btn btn-info btn-xs"
                data-placement="bottom" data-toggle="popover"
                role="button"><span class="glyphicon glyphicon-option-horizontal"></span></button>
        ${folder.path}
      </#if>
      </h2>
    </section>

    <#flush>

    <section>
      <div id="fileinfo" class="table-responsive">
        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <div class="collapse navbar-collapse" id="navbar-functions">
              <ul class="nav navbar-nav">
                <li>
                  <a id="level-up" name="level-up" href="/beacon/list${folder.parentPath}" role="button" title="Up one level">
                    <span class="glyphicon glyphicon-level-up"></span>&nbsp;Up</a></li>
                <li>
                  <a id="home" name="home" type="button" title="Navigate to main root." href="/beacon/list/">
                    <span class="glyphicon glyphicon-home"></span>&nbsp;Root</a></li>
                <li class="dropdown">
                  <a title="New" class="dropdown-toggle" role="button" id="newdropdown" name="newdropdown" aria-expanded="false" data-toggle="dropdown">
                    <span class="glyphicon glyphicon-plus"></span>&nbsp;New&nbsp;<span class="caret"></span></a>
                  <ul class="dropdown-menu">
                    <li>
                      <a id="newfolder">
                        <span class="glyphicon glyphicon-folder-open"></span>&nbsp;Folder</a></li>
                    <li>
                      <a id="newupload">
                        <span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;File</a>
                      <form id="uploader" method="post" class="hidden">
                        <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
                      </form>
                    </li>
                    <li>
                      <a id="newlink"><span class="glyphicon glyphicon-link"></span>&nbsp;Link</a></li>
                  </ul>
                </li>
              </ul>
              <div class="dataTables_filter" id="beacon_filter">
                <form class="navbar-form navbar-left" role="search">
                  <input id="beacon_filter" class="form-control dataTables_filter"
                         aria-controls="beacon" placeholder="Search Name..." type="search" />
                </form>
              </div>
            </div>
          </div>
        </nav>
        <table id="beacon" class="table table-striped table-condensed table-hover">
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>Size</th>
              <th>Last Modified (UTC)</th>
              <th>Read/Write</th>
              <th>Read</th>
            </tr>
          </thead>
          <tbody>
          <#list folder.childIterator as childItem>
            <#assign uri = childItem.URI>
            <#assign writeGroupNames = childItem.writeGroupNames>
            <tr>
              <td class="select-checkbox"></td>
              <td><span class="glyphicon ${childItem.itemIconCSS}"></span> <a href="/beacon${childItem.linkURI}"> ${childItem.name}</a></td>
              <td data-val="${childItem.sizeInBytes}">${childItem.sizeHumanReadable}</td>
              <td>${childItem.lastModifiedHumanReadable}</td>
              <td>${writeGroupNames}</td>
              <td><#if childItem.public><a href="#" class="public_link" title="Change group read access.">Public</a><#else>${childItem.readGroupNames}</#if></td>
            </tr>
          </#list>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</div>

<#--<div class="row">-->
  <#--<form id="uploader" method="post" class="mrgn-rght-md mrgn-lft-sm">-->
    <#--<h1 title="${folder.path}">${folder.path}</h1>-->
    <#--<div id="uploadresponse"></div>-->
    <#--<input id="mode" name="mode" type="hidden" value="add" />-->
    <#--<input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>-->
    <#--<div id="file-input-container" class="wb-inv">-->
    <#--&lt;#&ndash;<div id="alt-fileinput">&ndash;&gt;-->
    <#--&lt;#&ndash;<input id="filepath" name="filepath" type="text" />&ndash;&gt;-->
    <#--&lt;#&ndash;<button id="browse" name="browse" type="button" class="btn" value="Browse"></button>&ndash;&gt;-->
    <#--&lt;#&ndash;</div>&ndash;&gt;-->
      <#--<input id="newfile" name="newfile" type="file" />-->
    <#--</div>-->
  <#--&lt;#&ndash;<button id="upload" name="upload" type="submit" value="Upload" class="em"><span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;Upload</button>&ndash;&gt;-->

  <#--</form>-->
<#--</div>-->

