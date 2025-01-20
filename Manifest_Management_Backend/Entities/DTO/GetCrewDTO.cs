using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.Entities.DTO
{
    public class GetCrewoperatorDTO
    {
        public string Salutation { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }
        public string ContactNumber { get; set; }

        public DateTime Dob { get; set; }


        public string Gender { get; set; }

        public string CrewoperatorUsername { get; set; }

        public string Email { get; set; }
    }
}
