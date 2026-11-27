package com.gondroid.subtrack.feature.createsubscription

import com.gondroid.subtrack.feature.createsubscription.WizardStep.DETAILS
import com.gondroid.subtrack.feature.createsubscription.WizardStep.MEMBERS
import com.gondroid.subtrack.feature.createsubscription.WizardStep.SERVICE
import com.gondroid.subtrack.feature.createsubscription.WizardStep.SPLIT
import com.gondroid.subtrack.feature.createsubscription.WizardStep.WHO_PAYS
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class WizardStepsTest {

    @Test fun personalWizardHasThreeStepsAndSubmitsAfterDetails() {
        assertEquals(listOf(SERVICE, WHO_PAYS, DETAILS), wizardSteps(isShared = false))
        assertEquals(WHO_PAYS, nextWizardStep(SERVICE, isShared = false))
        assertEquals(DETAILS, nextWizardStep(WHO_PAYS, isShared = false))
        assertNull(nextWizardStep(DETAILS, isShared = false))
    }

    @Test fun sharedWizardHasFiveStepsAndSubmitsAfterSplit() {
        assertEquals(listOf(SERVICE, WHO_PAYS, DETAILS, MEMBERS, SPLIT), wizardSteps(isShared = true))
        assertEquals(MEMBERS, nextWizardStep(DETAILS, isShared = true))
        assertEquals(SPLIT, nextWizardStep(MEMBERS, isShared = true))
        assertNull(nextWizardStep(SPLIT, isShared = true))
    }

    @Test fun editingOnlyShowsTheDetailsStepAndSavesFromIt() {
        assertEquals(listOf(DETAILS), wizardSteps(isShared = true, isEditing = true))
        assertNull(nextWizardStep(DETAILS, isShared = true, isEditing = true))
        assertNull(previousWizardStep(DETAILS, isShared = false, isEditing = true))
    }

    @Test fun backFromDetailsReturnsToWhoPays() {
        assertEquals(WHO_PAYS, previousWizardStep(DETAILS, isShared = false))
        assertEquals(WHO_PAYS, previousWizardStep(DETAILS, isShared = true))
        assertEquals(SERVICE, previousWizardStep(WHO_PAYS, isShared = true))
        assertNull(previousWizardStep(SERVICE, isShared = false))
    }
}
