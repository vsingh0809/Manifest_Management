﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.Entities.DTO
{
    public class ResetPasswordRequest
    {

        public string Email { get; set; }
        public string Otp { get; set; }
        public string NewPassword { get; set; }

    }
}
