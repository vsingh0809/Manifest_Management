using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.Entities.DTO
{
    public class PasswordVerificationRequest
    {
        public string OldPassword { get; set; }
        public int Id { get; set; } // Use Id instead of email
    }

}
