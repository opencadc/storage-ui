<#assign startTime = .now?time>
<#assign isRoot = folder.root>
<div class="row">
  <div role="navigation" class="col-sm-3 col-md-2 sidebar">
    <#if homeURL??>
      <h3><a href="${homeURL}">VOSpace</a></h3>
    <#else>
      <h3><a href="/beacon/list">VOSpace</a></h3>
    </#if>
    <form id="uploader" method="post">
      <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
      <ul class="list-group menu list-unstyled">
        <li>
          <h4 class="active">Manage</h4>
          <ul class="list-group menu list-unstyled">
            <li><a class="list-group-item text-right" href="/canfar/groups">
              <span class="glyphicon glyphicon-user"></span>&nbsp;Groups</a></li>
          </ul>
        </li>
      </ul>
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
        ${folder.path}
        &nbsp;
      </#if>
      </h2>
      <ul class="btn-toolbar list-inline mrgn-bttm-sm" role="toolbar">
        <li class="btn-group">
          <a id="level-up" name="level-up" class="btn btn-default btn-md" href="/beacon/list${folder.parentPath}" role="button" <#if isRoot>disabled="disabled" title="Not permitted at ROOT level"<#else>title="Up one level"</#if>>
            <span class="glyphicon glyphicon-arrow-up"></span>&nbspUp</a>
        <#if homeURL??>
          <a id="home" name="home" type="button" class="btn btn-default btn-md" title="Navigate to Home directory for ${username}" href="${homeURL}">
            <span class="glyphicon glyphicon-home"></span>&nbsp;Home</a>
        </#if>
        </li>
        <li class="btn-group">
          <button id="newdropdown" name="newdropdown" type="button" class="btn btn-default btn-md dropdown-toggle" aria-expanded="false" data-toggle="dropdown" <#if isRoot>disabled="disabled" title="Not permitted at ROOT level"<#else>title="New folder"</#if>>
            <span class="glyphicon glyphicon-plus"></span>&nbsp;New&nbsp;<span class="caret"></span></button>
            <ul class="dropdown-menu">
              <li><a id="newfolder"><span class="glyphicon glyphicon-folder-open"></span>&nbsp;Folder</a></li>
              <li><a id="newupload"><span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;File</a></li>
              <li><a id="newlink"><span class="glyphicon glyphicon-link"></span>&nbsp;Link</a></li>
            </ul>
          <#--<button id="uploadfile" name="uploadfile" type="button" class="btn btn-default btn-md" <#if isRoot>disabled="disabled" title="Not permitted at ROOT level"<#else>title="Upload file"</#if>>-->
            <#--<span class="glyphicon glyphicon-cloud-upload"></span></button>-->
          <#--<button id="newlink" name="newlink" type="button" class="btn btn-default btn-md" <#if isRoot>disabled="disabled" title="Not permitted at ROOT level"<#else>title="New link"</#if>>-->
            <#--<span class="glyphicon glyphicon-link"></span></button>-->
        </li>
      </ul>
    </section>

    <#flush>

    <section>
      <div id="fileinfo" class="table-responsive">
        <table id="beacon" class="table table-striped table-condensed table-hover">
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>Size</th>
              <th>Read/Write</th>
              <th>Read</th>
              <th>Last Modified (UTC)</th>
            </tr>
          </thead>
          <tbody>
          <#list folder.childIterator as childItem>
            <#assign uri = childItem.URI>
            <#assign writeGroupNames = childItem.writeGroupNames>
            <tr>
              <td class="select-checkbox"></td>
              <td><span class="glyphicon ${childItem.itemIconCSS}"></span> <a href="/beacon${childItem.linkURI}"> ${childItem.name}</a> </td>
              <td data-val="${childItem.sizeInBytes}">${childItem.sizeHumanReadable}</td>
              <td>${writeGroupNames}</td>
              <td><#if childItem.public><a href="#" class="public_link" title="Change group read access.">Public</a><#else>${childItem.readGroupNames}</#if></td>
              <td>${childItem.lastModifiedHumanReadable}</td>
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

