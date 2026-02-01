package com.example.weatherapp


import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private  val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        fetchWeatherData("Delhi")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
             return true
            }

        })
    }

    private fun fetchWeatherData(CityName:String) {




            val api = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)

            val call = api.getWeatherData(
                CityName,
                "c09816d6aaeb8c68999400967327c786",
                "metric"
            )

            call.enqueue(object : Callback<WeatherApp> {

                override fun onResponse(
                    call: Call<WeatherApp>,
                    response: Response<WeatherApp>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val  sunset= response.body()!!.sys.sunset
                        binding.sunset.text = time(sunset)
                        val sunrise = response.body()!!.sys.sunrise
                        binding.Sunrise.text = time(sunrise)


                        binding.Temp.text = "${response.body()!!.main.temp.toString()}°C"
                        binding.Humidity.text = "${response.body()!!.main.humidity} %"
                        binding.Wind.text = "${response.body()!!.wind.speed} m/s"


                        binding.Sea.text = "${response.body()!!.main.pressure} hPa"
                        binding.Condition.text = "${response.body()!!.weather[0].main}"
                        binding.weather.text = "${response.body()!!.weather[0].main}"
                        binding.MaxTemp.text = "${response.body()!!.main.temp_max}"
                        binding.mintemp.text = "${response.body()!!.main.temp_min}"
                        binding.day.text = DayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.CityName.text= "$CityName"




                        val condition = "${response.body()!!.weather[0].main}"
                        //ImageChangeAccToWeather(weather.weather[0].main)

                        ImageChangeAccToWeather(condition)
                    }
                }

                override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

                    Log.d("Failed", "onFailure:  ${t.message}")
                    Toast.makeText(
                        this@MainActivity,
                        "Failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }


    private fun ImageChangeAccToWeather(condition: String) {

        when (condition.lowercase()) {

            "clear", "sunny" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "clouds", "mist", "fog", "haze", "overcast" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "rain", "drizzle", "shower", "heavy rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "snow", "blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }


    fun DayName(timestamp :Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }


    }
