package app.tuxguitar.android.view.util

open class TGSelectableItem(
    private val item: Any?,
    private val label: String?,
    private val dropDownLabel: String? = label,
) {
    fun getItem(): Any? = item

    fun getLabel(): String? = label

    fun getDropDownLabel(): String? = dropDownLabel

    override fun toString(): String = getLabel() ?: ""

    override fun equals(other: Any?): Boolean {
        if (other is TGSelectableItem) {
            val item1 = getItem()
            val item2 = other.getItem()
            return if (item1 != null && item2 != null) item1 == item2 else item1 === item2
        }
        return false
    }

    override fun hashCode(): Int = getItem()?.hashCode() ?: 0
}
