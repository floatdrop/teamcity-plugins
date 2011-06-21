package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.CustomTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.SimplePageExtension
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.controller.MessagesSendController

/**
 * Messenger extension
 */
class MessagesSendExtension extends SimplePageExtension implements CustomTab
{
    private final SBuildServer     server
    private final UserGroupManager groupsManager


    @Requires({ server && groupsManager && pagePlaces && context })
    MessagesSendExtension ( SBuildServer server, UserGroupManager groupsManager, PagePlaces pagePlaces, MessagesContext context )
    {
        super( pagePlaces, PlaceId.MY_TOOLS_TABS, context.pluginName, 'messagesSend.jsp' )
        register()

        this.server        = server
        this.groupsManager = groupsManager

        addJsFile(  'js/jquery-ui-1.8.13.custom.min.js' )
        addJsFile(  'js/jquery-plugins.min.js' )
        addJsFile(  'js/messages-send.min.js' )

        addCssFile( 'css/custom-theme/jquery-ui-1.8.13.custom.css' )
        addCssFile( 'css/messenger-plugin.css' )
    }


    @Override
    @Requires({ ( model != null ) && server && groupsManager })
    @Ensures({ model })
    void fillModel ( Map<String, Object> model, HttpServletRequest request )
    {
        def groups = groupsManager.userGroups*.name.findAll{ it }
        def users  = server.userModel.allUsers.users*.username.findAll{ it }

        assert groups, 'No groups found on the server'
        assert users,  'No users found on the server'

        model << [ groups : groups,
                   users  : users,
                   action : MessagesSendController.MAPPING ]
    }


    String getTabId    () { 'sendMessage'  }
    String getTabTitle () { 'Send Message' }
    boolean isVisible  () { true }
}
