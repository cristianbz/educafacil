/**
 * 
 */
window.fbAsyncInit = function() {
		    FB.init({
		      appId            : 'your-app-id',
		      xfbml            : true,
		      version          : 'v18.0'
		    });
};

FB.login(function(response) {
		    if (response.authResponse) {
		     console.log('Welcome!  Fetching your information.... ');
		     FB.api('/me', function(response) {
		       console.log('Good to see you, ' + response.name + '.');
		     });
		    } else {
		     console.log('User cancelled login or did not fully authorize.');
		    }
});