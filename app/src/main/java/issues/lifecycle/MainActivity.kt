package issues.lifecycle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    private val debugMessages by lazy {
        findViewById<TextView>(R.id.debug_messages)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var counter = 0
        findViewById<View>(R.id.test).setOnClickListener {
            replace(DebugFragment.create("Fragment${counter++}"))
        }
    }

    @SuppressLint("SetTextI18n")
    fun addMessage(message: String) {
        debugMessages.text = "${debugMessages.text ?: ""}\n$message"
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            // To work around issue remove transition or downgrade (1.1.0 is ok, didn't check other versions)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}

class DebugFragment : Fragment() {

    private lateinit var payload: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payload = requireNotNull(requireArguments().getString(keyPayload))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        addMessage("onCreateView()")
        return View(requireContext())
    }

    override fun onDestroyView() {
        addMessage("onDestroyView()")
        super.onDestroyView()
    }

    private fun addMessage(message: String) {
        (requireActivity() as MainActivity).addMessage("$payload: $message")
    }

    companion object {

        private const val keyPayload = "payload"

        fun create(payload: String): DebugFragment {
            return DebugFragment().apply {
                arguments = Bundle().apply {
                    putString(keyPayload, payload)
                }
            }
        }
    }
}
