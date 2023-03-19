package com.example.dicegame

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.dicegame.databinding.FragmentPlayGameBinding
import com.google.android.material.color.utilities.Score
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [playGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class playGameFragment : Fragment() {

    private lateinit var playerdicesList: MutableList<ImageView>
    private lateinit var computerdicesList: MutableList<ImageView>
    private lateinit var binding: FragmentPlayGameBinding

    // shuffle scoreholders
    private var playershuffleScore: Int = 0
    private var computershuffleScore: Int = 0

    //    shuffle counter
    var throwCounter = 0

    var winningScore =101


    //    temporary player score holder for each throw
    private var playerEachThrowScore = 0

    //previous throw value holder
    var previousDiceValueArr: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5);


    var playerDicesRollability: MutableList<Boolean> = mutableListOf(true, true, true, true, true)


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_game, container, false)

        // player dices list
        playerdicesList =
            mutableListOf(binding.pd1, binding.pd2, binding.pd3, binding.pd4, binding.pd5)

//        call eventListeners
        addEventListnersToDices()

        //when throw button pressed
        binding.shuffle.setOnClickListener {
            throwCounter++

            // checking 2nd throw
            if (throwCounter < 3) {
                playerEachThrowScore = throwDices(playerdicesList, true)


                Log.d("check throw count", throwCounter.toString())

                //in the 3rd throw ,3rd throw score should be added

            } else if (throwCounter == 3) {
                playerEachThrowScore = throwDices(playerdicesList, player = true)
                computershuffleScore += throwDices(computerdicesList, player = false)
                playershuffleScore += playerEachThrowScore
                updateScores(playershuffleScore, computershuffleScore)
                checkingWinner(it)
                throwCounter = 0
                Log.d("check throw count", throwCounter.toString())
            }
        }

        //computer dices list
        computerdicesList = mutableListOf(binding.cd1, binding.cd2, binding.cd3, binding.cd4, binding.cd5)

        //when score button pressed
        binding.Score.setOnClickListener {
            if (throwCounter != 0) {
                playershuffleScore += playerEachThrowScore
                computershuffleScore += throwDices(computerdicesList, player = false)
                updateScores(playershuffleScore, computershuffleScore)
                checkingWinner(it)
                throwCounter = 0

            } else {
                Toast.makeText(activity, "Press Throw Button First!!", Toast.LENGTH_SHORT).show();
            }
        }

        //Enter score in the edit text
        binding.UpdateScoreBtn.setOnClickListener {
            Log.d("check update the score",winningScore.toString())
            winningScore=binding.winScore.text.toString().toInt()
            binding.winScore.visibility=View.GONE
            binding.UpdateScoreBtn.visibility=View.GONE

        }

        return binding.root
    }

    private fun rollDice(imgR: ImageView): Int {
        val imageNumber = (Random.nextInt(6) + 1);
        val imageResource = when (imageNumber) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        imgR.setImageResource(imageResource)
        return imageNumber

    }

    private fun throwDices(array: MutableList<ImageView>, player: Boolean): Int {
        var total = 0
        var count = 0
        if (player) {
            throwCounter += 0
        }
//shuffle all the dices
        for (img in array) {
            if (player) {
                img.alpha = 1f
//get the total of throw
                if (playerDicesRollability[count]) {
                    val value = rollDice(img)
                    previousDiceValueArr[count] = value
                    total += value

                } else {
                    total += previousDiceValueArr[count]
                    playerDicesRollability[count] = true

                }
            } else {
                val value = rollDice(img)
                total += value
            }
            count++
        }

        return total

    }

    //updating scores
    private fun updateScores(playerScore: Int, computerScore: Int) {
        binding.yourScroreValue.setText(playerScore.toString())
        binding.computerScoreValue.setText(computerScore.toString())


    }

    //checking winner
    private fun checkingWinner(view: View) {
        if (playershuffleScore >= winningScore || computershuffleScore >= winningScore) {
            if (playershuffleScore < computershuffleScore) {
                // player lost
                Navigation.findNavController(view)
                    .navigate(R.id.action_playGameFragment_to_lostFragment)
            } else if (computershuffleScore < playershuffleScore) {
                // player wins
                Navigation.findNavController(view)
                    .navigate(R.id.action_playGameFragment_to_wonFragment)

            }
        }
    }

    private fun changeAlpha(imgR: ImageView){

        imgR.alpha = 0.4f
    }

    //    add event listeners
    private fun addEventListnersToDices() {
            Log.d("eventlistener", throwCounter.toString())
            for (img in playerdicesList) {
                img.setOnClickListener {
                    if(throwCounter != 0){
                        val clickedId = it.id  //from this id we are generating id(index) for each dice
                        val arrayIndex = when (clickedId) {
                            2131231063 -> 0
                            2131231064 -> 1
                            2131231065 -> 2
                            2131231066 -> 3
                            else -> 4
                        }
                        changeAlpha(playerdicesList[arrayIndex]) //alpha used to show the dice is  in object dice array
                        playerDicesRollability[arrayIndex] = false
                    }
                    else{
                        Toast.makeText(activity, "Press Throw Button First!!", Toast.LENGTH_SHORT).show();
                    }

                }
            }


    }


}