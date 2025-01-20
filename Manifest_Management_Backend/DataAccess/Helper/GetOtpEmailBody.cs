using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.DataAccess.Helper
{
    public static class EmailTemplateHelper
    {
        public static string GetOtpEmailBody(string otp)
        {
            return $@"
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='text-align: center; padding: 10px; border: 1px solid #ddd; border-radius: 8px; max-width: 600px; margin: auto; background-color: #f9f9f9;'>
                    <h2 style='color: #333;'>Welcome to ManifestManagment!</h2>
                    <p style='font-size: 16px; color: #555;'>
                        We're excited to have you onboard. To verify your email address, please use the following One-Time Password (OTP):
                    </p>
                    <h3 style='font-size: 24px; color: #4CAF50; margin: 20px 0;'>{otp}</h3>
                    <p style='font-size: 14px; color: #777;'>
                        This code is valid for the next 10 minutes. If you didn't request this, please ignore this email.
                    </p>
                    <hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;' />
                    <p style='font-size: 12px; color: #999;'>
                        © 2024 ManifestManagment. All rights reserved.
                    </p>
                </div>
            </body>
            </html>";
        }
    }

}
