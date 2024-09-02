package com.an.notesapp

object Constants {
    const val ROUTE_HOME = "home"
    private const val ROUTE_DETAIL = "detail"
    const val ROUTE_DETAIL_ARG_NAME = "noteId"

    const val ROUTE_DETAIL_PATH = "$ROUTE_DETAIL?$ROUTE_DETAIL_ARG_NAME={$ROUTE_DETAIL_ARG_NAME}"
}