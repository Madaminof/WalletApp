package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity


// ParticipantWithAssignments (Ishtirokchi va uning xarajatlari)
//Bu model orqali aniq bir kishiga (masalan, "Ali") qaysi mahsulotlar biriktirilganini bilish mumkin.


data class ParticipantWithAssignments(
    @Embedded val participant: ParticipantEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "participantId"
    )
    val assignments: List<ItemAssignmentEntity>
)