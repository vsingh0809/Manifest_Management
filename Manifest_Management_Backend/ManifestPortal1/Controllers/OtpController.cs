
namespace ManifestPortal1.Controllers
{
    using Microsoft.AspNetCore.Mvc;
    using Microsoft.Extensions.Caching.Memory;
    using MailKit.Net.Smtp;
    using MimeKit;
    using System;
    using System.Threading.Tasks;
    using ManifestPortal1.Entities.DTO;


    [ApiController]
    [Route("api/otp")]
    public class OtpController : ControllerBase
    {
        private readonly IMemoryCache _cache;
        //private readonly OtpRequest otpRequest;
        //private readonly ResetPasswordRequest resetPasswordRequest;

        public OtpController(IMemoryCache cache)
        {
            _cache = cache;
        }

        [HttpPost("send")]
        public async Task<IActionResult> SendOtp([FromBody] string email)
        {
            var otp = new Random().Next(100000, 999999).ToString();
            _cache.Set(email, otp, TimeSpan.FromMinutes(5)); // Store OTP with a 5-minute expiration

            await SendEmailAsync(email, otp);
            return Ok();
        }




        private async Task SendEmailAsync(string email, string otp)
        {
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress("ManifestManagment", "your-email@gmail.com"));
            message.To.Add(new MailboxAddress("", email));
            message.Subject = "ManifestManagment - Verify Your Email with OTP";

            // Create a formatted HTML email body
            string htmlBody = DataAccess.Helper.EmailTemplateHelper.GetOtpEmailBody(otp);

            // Set the message body as HTML
            message.Body = new TextPart("html")
            {
                Text = htmlBody
            };

            using (var client = new SmtpClient())
            {
                await client.ConnectAsync("smtp.gmail.com", 587, MailKit.Security.SecureSocketOptions.StartTls);
                await client.AuthenticateAsync("Vaibhav.gyn@gmail.com", "anaiilcdvrujhdnr");
                await client.SendAsync(message);
                await client.DisconnectAsync(true);
            }

        }

        [HttpPost("verify")]
        public IActionResult VerifyOtp([FromBody] OtpRequest request)
        {
            if (_cache.TryGetValue(request.Email, out string storedOtp) && storedOtp == request.Otp)
            {
                return Ok(true);
            }

            return BadRequest("Invalid or expired OTP");
        }

        [HttpPost("forgot-password")]
        public IActionResult ForgotPassword([FromBody] ResetPasswordRequest request)
        {
            if (_cache.TryGetValue(request.Email, out string storedOtp) && storedOtp == request.Otp)
            {
                // Update password logic here (e.g., save the new password to the database)
                // Ensure you hash the password before saving it
                return Ok(true);
            }

            return BadRequest("Invalid OTP or OTP expired");
        }
    }


}
