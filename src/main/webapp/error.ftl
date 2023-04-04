<#if mode??>
  <#if mode == "dev">
    <#include "_default_error.ftl">
  <#else>
    <#include "_canfar_error.ftl">
  </#if>
<#else>
  <#include "_canfar_error.ftl">
</#if>