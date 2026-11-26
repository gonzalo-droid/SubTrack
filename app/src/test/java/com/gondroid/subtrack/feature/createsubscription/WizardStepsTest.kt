package com.gondroid.subtrack.feature.createsubscription

import com.gondroid.subtrack.feature.createsubscription.WizardStep.DETAILS
import com.gondroid.subtrack.feature.createsubscription.WizardStep.MEMBERS
import com.gondroid.subtrack.feature.createsubscription.WizardStep.SERVICE
import com.gondroid.subtrack.feature.createsubscription.WizardStep.SPLIT
import com.gondroid.subtrack.feature.createsubscription.WizardStep.WHO_PAYS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WizardStepsTest {

    @Test fun `personal wizard has three steps and submits after details`() {
        assertEquals(listOf(SERVICE, WHO_PAYS, DETAILS), wizardSteps(isShared = false))
        assertEquals(WHO_PAYS, nextWizardStep(SERVICE, isShared = false))
        assertEquals(DETAILS, nextWizardStep(WHO_PAYS, isShared = false))
        assertNull(nextWizardStep(DETAILS, isShared = false))
    }

    @Test fun `shared wizard has five steps and submits after split`() {
        assertEquals(listOf(SERVICE, WHO_PAYS, DETAILS, MEMBERS, SPLIT), wizardSteps(isShared = true))
        assertEquals(MEMBERS, nextWizardStep(DETAILS, isShared = true))
        assertEquals(SPLIT, nextWizardStep(MEMBERS, isShared = true))
        assertNull(nextWizardStep(SPLIT, isShared = true))
    }

    @Test fun `back from details returns to who pays`() {
        assertEquals(WHO_PAYS, previousWizardStep(DETAILS, isShared = false))
        assertEquals(WHO_PAYS, previousWizardStep(DETAILS, isShared = true))
        assertEquals(SERVICE, previousWizardStep(WHO_PAYS, isShared = true))
        assertNull(previousWizardStep(SERVICE, isShared = false))
    }
}
