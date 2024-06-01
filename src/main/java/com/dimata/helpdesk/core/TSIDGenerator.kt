package com.dimata.helpdesk.core

import io.hypersistence.tsid.TSID

fun generateTSID() = TSID.Factory.getTsid().toString()