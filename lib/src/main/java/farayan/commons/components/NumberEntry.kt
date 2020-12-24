package farayan.commons.components

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import farayan.commons.components.number.entry.R
import java.text.DecimalFormat
import java.text.ParseException
import kotlin.properties.Delegates

class NumberEntry : AppCompatEditText {
    private var group by Delegates.notNull<Boolean>();
    private var decimalPrecision by Delegates.notNull<Int>();
    private var negativeSupported by Delegates.notNull<Boolean>();
    private lateinit var localNumberProvider: ILocalNumberProvider;

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadAttrs(attrs, 0)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadAttrs(attrs, defStyleAttr)
        init()
    }

    private fun loadAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberEntry, defStyleAttr, 0)
        try {
            val localProviderClassName = typedArray.getString(R.styleable.NumberEntry_localizeProviderClassName);
            localNumberProvider = if (localProviderClassName.isNullOrBlank()) {
                PersianLocalNumberProvider();
            } else {
                val classLoader = this.javaClass.classLoader;
                classLoader.loadClass(localProviderClassName).getDeclaredConstructor().newInstance() as ILocalNumberProvider;
            }
            group = typedArray.getBoolean(R.styleable.NumberEntry_group, true);
            decimalPrecision = typedArray.getInt(R.styleable.NumberEntry_decimalPrecision, 0);
            negativeSupported = typedArray.getBoolean(R.styleable.NumberEntry_negativeSupported, false);
        } finally {
            typedArray.recycle();
        }
    }

    private fun init() {
        var inputType = InputType.TYPE_CLASS_NUMBER
        if (decimalPrecision > 0) inputType = inputType or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (negativeSupported) inputType = inputType or InputType.TYPE_NUMBER_FLAG_SIGNED
        setInputType(inputType)
        layoutDirection = LAYOUT_DIRECTION_LTR
        gravity = Gravity.CENTER_HORIZONTAL
        addTextChangedListener(NumberTextWatcher(this, localNumberProvider, decimalPrecision))
    }

    private fun textual(value: Number?): String {
        val formatter = ensuredFormatter();
        var formatted = formatter.format(value);
        if (formatted.contains(".") && formatted.endsWith("0"))
            formatted = formatted.trimEnd { c -> c == '0' }
        return formatted;
    }

    lateinit var defaultFormatter: DecimalFormat;
    private fun ensuredFormatter(): DecimalFormat {
        var precision = "";
        for (index in 1..decimalPrecision)
            precision += "#";

        defaultFormatter = DecimalFormat("###.$precision")
        return defaultFormatter;
    }

    private fun plainText(): String {
        var plain = localNumberProvider
                .globalize(text.toString())
                .replace(",", "")
        return plain;
    }

    var nullableDouble: Double?
        get() = plainText().toDoubleOrNull();
        set(value) {
            setText(textual(value));
        }

    var doubleValue: Double
        get() = plainText().toDoubleOrNull() ?: 0.0;
        set(value) {
            setText(textual(value));
        }

    fun getDoubleValue(defaultValue: Double): Double {
        return nullableDouble ?: defaultValue;
    }

    var nullableLong: Long?
        get() = plainText().toLongOrNull();
        set(value) {
            setText(textual(value));
        }

    var longValue: Long
        get() = plainText().toLongOrNull() ?: 0;
        set(value) {
            setText(textual(value));
        }

    fun getLongValue(defaultValue: Long): Long {
        return nullableLong ?: defaultValue;
    }

    var nullableInt: Int?
        get() = plainText().toIntOrNull();
        set(value) {
            setText(textual(value));
        }

    var intValue: Int
        get() = plainText().toIntOrNull() ?: 0;
        set(value) {
            setText(textual(value));
        }

    fun getIntValue(defaultValue: Int): Int {
        return nullableInt ?: defaultValue;
    }

    var nullableFloat: Float?
        get() = plainText().toFloatOrNull();
        set(value) {
            setText(textual(value));
        }

    var floatValue: Float
        get() = plainText().toFloatOrNull() ?: 0.0F;
        set(value) {
            setText(textual(value));
        }

    fun getFloatValue(defaultValue: Float): Float {
        return nullableFloat ?: defaultValue;
    }

    inner class NumberTextWatcher(
            editText: EditText,
            localNumberProvider: ILocalNumberProvider,
            decimalPrecision: Int
    ) : TextWatcher {
        private val fractionalFormatter: DecimalFormat
        private val plainFormatter: DecimalFormat
        private var hasFractionalPart = false
        private var number: Number? = null
        private val editText: EditText
        private val localNumberProvider: ILocalNumberProvider

        override fun afterTextChanged(editable: Editable) {
            // VERY IMPORTANT
            editText.removeTextChangedListener(this)
            try {
                val beginningLength: Int = editText.text.length
                val plain = editable
                        .toString()
                        .replace(localNumberProvider.groupSeparator(), "")
                        .replace(localNumberProvider.decimalPoint(), ".")
                number = fractionalFormatter.parse(plain)
                val cp = editText.selectionStart
                val grouped = (if (hasFractionalPart) fractionalFormatter else plainFormatter).format(number)
                val localized = localNumberProvider.localize(grouped);
                editText.setText(localized)
                val finishingLength: Int = editText.text.length
                val sel = cp + (finishingLength - beginningLength)
                if (sel > 0 && sel <= editText.text.length) {
                    editText.setSelection(sel)
                } else {
                    // place cursor at the end?
                    editText.setSelection(editText.text.length - 1)
                }
            } catch (nfe: NumberFormatException) {
                // do nothing?
            } catch (nfe: ParseException) {
            }

            // VERY IMPORTANT
            editText.addTextChangedListener(this)
            onChanged();
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            hasFractionalPart = s.toString().contains(localNumberProvider.decimalPoint())
        }

        init {
            var precision = "";
            for (index in 1..decimalPrecision)
                precision += "#";
            val fractionalFormat = "#,###.$precision";
            fractionalFormatter = DecimalFormat(fractionalFormat)
            fractionalFormatter.isDecimalSeparatorAlwaysShown = true

            val plainFormat = "#,###";
            plainFormatter = DecimalFormat(plainFormat)
            this.editText = editText
            this.localNumberProvider = localNumberProvider;
        }
    }

    private fun onChanged() {
        onDoubleValueChanged?.onChanged(doubleValue);
        onNullableDoubleChanged?.onChanged(nullableDouble);
        onLongValueChanged?.onChanged(longValue);
        onNullableLongChanged?.onChanged(nullableLong);
        onIntValueChanged?.onChanged(intValue);
        onNullableIntChanged?.onChanged(nullableInt);
        onFloatValueChanged?.onChanged(floatValue);
        onNullableFloatChanged?.onChanged(nullableFloat);
    }

    interface IChangedEvent<TNumber> {
        fun onChanged(number: TNumber);
    }

    var onDoubleValueChanged: IChangedEvent<Double>? = null;
    var onNullableDoubleChanged: IChangedEvent<Double?>? = null;
    var onLongValueChanged: IChangedEvent<Long>? = null;
    var onNullableLongChanged: IChangedEvent<Long?>? = null;
    var onIntValueChanged: IChangedEvent<Int>? = null;
    var onNullableIntChanged: IChangedEvent<Int?>? = null;
    var onFloatValueChanged: IChangedEvent<Float>? = null;
    var onNullableFloatChanged: IChangedEvent<Float?>? = null;

    interface ILocalNumberProvider {
        fun localize(input: String): String
        fun globalize(input: String): String
        fun groupSeparator(): String
        fun decimalPoint(): String
    }

    class ArabicLocalNumberProvider : ILocalNumberProvider {
        private val zero: String = "٠";
        private val one: String = "١";
        private val two: String = "٢";
        private val three: String = "٣";
        private val four: String = "٤";
        private val five: String = "٥";
        private val six: String = "٦";
        private val seven: String = "٧";
        private val eight: String = "٨";
        private val nine: String = "٩";

        override fun localize(input: String): String {
            return input
                    .replace("0", zero)
                    .replace("1", one)
                    .replace("2", two)
                    .replace("3", three)
                    .replace("4", four)
                    .replace("5", five)
                    .replace("6", six)
                    .replace("7", seven)
                    .replace("8", eight)
                    .replace("9", nine)
                    .replace(".", decimalPoint())
                    .replace(",", groupSeparator())
        }

        override fun globalize(input: String): String {
            return input
                    .replace(zero, "0")
                    .replace(one, "1")
                    .replace(two, "2")
                    .replace(three, "3")
                    .replace(four, "4")
                    .replace(five, "5")
                    .replace(six, "6")
                    .replace(seven, "7")
                    .replace(eight, "8")
                    .replace(nine, "9")
                    .replace(decimalPoint(), ".")
                    .replace(groupSeparator(), ",")
        }

        override fun groupSeparator(): String {
            return "٫"
        }

        override fun decimalPoint(): String {
            return "."
        }
    }

    class PersianLocalNumberProvider : ILocalNumberProvider {
        private val zero: String = "۰";
        private val one: String = "۱";
        private val two: String = "۲";
        private val three: String = "۳";
        private val four: String = "۴";
        private val five: String = "۵";
        private val six: String = "۶";
        private val seven: String = "۷";
        private val eight: String = "۸";
        private val nine: String = "۹";

        override fun localize(input: String): String {
            return input
                    .replace("0", zero)
                    .replace("1", one)
                    .replace("2", two)
                    .replace("3", three)
                    .replace("4", four)
                    .replace("5", five)
                    .replace("6", six)
                    .replace("7", seven)
                    .replace("8", eight)
                    .replace("9", nine)
                    .replace(".", decimalPoint())
                    .replace(",", groupSeparator())
        }

        override fun globalize(input: String): String {
            return input
                    .replace(zero, "0")
                    .replace(one, "1")
                    .replace(two, "2")
                    .replace(three, "3")
                    .replace(four, "4")
                    .replace(five, "5")
                    .replace(six, "6")
                    .replace(seven, "7")
                    .replace(eight, "8")
                    .replace(nine, "9")
                    .replace(decimalPoint(), ".")
                    .replace(groupSeparator(), ",")
        }

        override fun groupSeparator(): String {
            return "٫"
        }

        override fun decimalPoint(): String {
            return "."
        }
    }
}
