package farayan.commons.components

class NumberEntryUtility {
    companion object {
        fun decimalsCount(decimalValue: String): Int {
            return (if (decimalValue.contains(".")) decimalValue.length - decimalValue.indexOf(".") - 1 else 0);
        }
    }
}
