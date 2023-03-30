<#if mode??>
  <#if mode = "dev">
    <#include "_default.ftl">
  <#else>
    <#include "_canfar.ftl">
  </#if>
<#else>
  <#include "_canfar.ftl">
</#if>